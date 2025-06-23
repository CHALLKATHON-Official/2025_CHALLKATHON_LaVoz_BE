package com.LaVoz.LaVoz.service;

import com.LaVoz.LaVoz.common.exception.ResourceNotFoundException;
import com.LaVoz.LaVoz.common.openai.OpenAiApiClient;
import com.LaVoz.LaVoz.common.openai.dto.ChatGptResponse;
import com.LaVoz.LaVoz.common.openai.dto.Message;
import com.LaVoz.LaVoz.common.openai.templates.PromptManager;
import com.LaVoz.LaVoz.domain.Member;
import com.LaVoz.LaVoz.domain.Note;
import com.LaVoz.LaVoz.domain.Organization;
import com.LaVoz.LaVoz.domain.Status;
import com.LaVoz.LaVoz.repository.MemberOrganizationRepository;
import com.LaVoz.LaVoz.repository.NoteRepository;
import com.LaVoz.LaVoz.repository.OrganizationRepository;
import com.LaVoz.LaVoz.repository.StatusRepository;
import com.LaVoz.LaVoz.web.apiResponse.error.ErrorStatus;
import com.LaVoz.LaVoz.web.dto.response.ChatGptStatusDto;
import com.LaVoz.LaVoz.web.dto.response.ChildStatusResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatGptService {

    private final OpenAiApiClient openAiApiClient;
    private final PromptManager promptManager;
    private final ObjectMapper objectMapper;
    private final StatusRepository statusRepository;
    private final NoteRepository noteRepository;
    private final OrganizationRepository organizationRepository;
    private final MemberOrganizationRepository memberOrganizationRepository;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.maxTokens}")
    private int maxTokens;

    @Value("${openai.temperature}")
    private double temperature;

    private final String systemRole = "system";
    private final String userRole = "user";

    /**
     * 상태 분석
     */
    public ChildStatusResponse analyzeChildState(Long organizationId, Member member) {

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorStatus.ORGANIZATION_NOT_FOUND));

        // 멤버 권한 확인
        boolean isOrganizationMember = memberOrganizationRepository.existsByMember_MemberIdAndOrganization_OrganizationId(member.getMemberId(), organizationId);
        if (!isOrganizationMember) {
            throw new ResourceNotFoundException(ErrorStatus.MEMBER_ORGANIZATION_NOT_FOUND);
        }

        // 현재 아이 상태 조회
        Status currentStatus = statusRepository.findTopByOrganizationOrderByCreatedAtDesc(organization)
                .orElse(null);

        if (currentStatus != null) {
            log.info("기존 상태 발견: Status ID = {}, 생성일 = {}", currentStatus.getStatusId(), currentStatus.getCreatedAt());
        } else {
            log.info("기존 상태가 없습니다. 새로운 분석을 시작합니다.");
        }

        List<Note> newNotes = getNewNotes(organization, currentStatus);
        log.info("분석 대상 노트 개수: {}, 노트 id: {}", newNotes.size(), newNotes.get(0).getNoteId());

        // 기존 상태가 있고 새로운 노트가 없는 경우 -> 기존 상태 반환 (GPT 호출 X)
        if (currentStatus != null && newNotes.isEmpty()) {
            log.info("기존 상태가 있고 새로운 노트가 없어 기존 상태를 반환합니다.");
            return convertToResponse(currentStatus, organization);
        }

        // 기존 상태가 없고 노트도 없는 경우 -> 에러 또는 빈 상태 처리
        if (currentStatus == null && newNotes.isEmpty()) {
            log.warn("기존 상태도 없고 분석할 노트도 없습니다.");
            throw new ResourceNotFoundException(ErrorStatus.NOTE_NOT_FOUND); // 적절한 에러 상태로 변경
        }

        // 새로운 노트가 있는 경우 (기존 상태 유무와 관계없이) -> GPT 분석 실행
        log.info("새로운 노트가 있어 ChatGPT 분석을 시작합니다.");

        // ChatGPT API 호출
        String prompt = promptManager.createChildStateAnalysisPrompt(currentStatus, newNotes);
        String responseContent = callChatGptApi(prompt);

        // JSON 응답을 DTO로 변환
        ChatGptStatusDto chatGptDto = parseJsonResponse(responseContent);

        // 데이터베이스에 저장
        Status savedStatus = saveStatusToDatabase(chatGptDto, organization);

        // 응답 DTO 생성 및 반환
        return convertToResponse(savedStatus, organization);
    }

    /**
     * 새로운 노트들 조회
     */
    private List<Note> getNewNotes(Organization organization, Status currentStatus) {
        if (currentStatus == null) {
            log.info("기존 상태가 없어 모든 노트를 조회합니다.");
            return noteRepository.findByOrganizationOrderByCreatedAtAsc(organization);
        } else {
            log.info("기존 상태 이후 노트를 조회합니다. 기준일: {}", currentStatus.getCreatedAt());
            List<Note> newNotes = noteRepository.findByOrganizationAndCreatedAtAfterOrderByCreatedAtAsc(
                    organization, currentStatus.getCreatedAt());
            log.info("새로운 노트 개수: {}", newNotes.size());

            // 새로운 노트들의 제목 로그
            newNotes.forEach(note -> log.debug("새로운 노트: {} (작성일: {})", note.getTitle(), note.getCreatedAt()));

            return newNotes;
        }
    }

    /**
     * ChatGPT API 호출
     */
    private String callChatGptApi(String prompt) {
        List<Message> messages = List.of(
                new Message(systemRole, "당신은 아동 행동 분석 전문가입니다. 주어진 정보를 바탕으로 정확하고 유용한 분석을 제공해주세요."),
                new Message(userRole, prompt)
        );

        ChatGptResponse chatGptResponse = openAiApiClient.sendRequestToModel(
                model, messages, maxTokens, temperature);

        String responseContent = chatGptResponse.getChoices().get(0).getMessage().getContent();
        log.info("ChatGPT 응답: {}", responseContent);

        return responseContent;
    }

    /**
     * JSON 응답을 DTO로 파싱
     */
    private ChatGptStatusDto parseJsonResponse(String jsonResponse) {
        try {
            // JSON에서 불필요한 부분 제거 (```json 등)
            String cleanJson = jsonResponse.trim();
            if (cleanJson.startsWith("```json")) {
                cleanJson = cleanJson.substring(7);
            }
            if (cleanJson.endsWith("```")) {
                cleanJson = cleanJson.substring(0, cleanJson.length() - 3);
            }

            return objectMapper.readValue(cleanJson, ChatGptStatusDto.class);
        } catch (Exception e) {
            log.error("JSON 파싱 오류: {}", e.getMessage());
            throw new RuntimeException("ChatGPT 응답 파싱 실패: " + e.getMessage());
        }
    }

    /**
     * 데이터베이스에 상태 저장
     */
    private Status saveStatusToDatabase(ChatGptStatusDto dto, Organization organization) {
        Status status = Status.builder()
                .morningEmotion(dto.getMorningEmotion())
                .morningBehavior(dto.getMorningBehavior())
                .afternoonEmotion(dto.getAfternoonEmotion())
                .afternoonBehavior(dto.getAfternoonBehavior())
                .nightEmotion(dto.getNightEmotion())
                .nightBehavior(dto.getNightBehavior())
                .happyBehaviorMap(dto.getHappyBehaviorMap())
                .sadBehaviorMap(dto.getSadBehaviorMap())
                .annoyingBehaviorMap(dto.getAnnoyingBehaviorMap())
                .hearingSensitivity(dto.getHearingSensitivity())
                .sightSensitivity(dto.getSightSensitivity())
                .touchSensitivity(dto.getTouchSensitivity())
                .smellSensitivity(dto.getSmellSensitivity())
                .tasteSensitivity(dto.getTasteSensitivity())
                .socialSensitivity(dto.getSocialSensitivity())
                .organization(organization)
                .build();

        Status savedStatus = statusRepository.save(status);
        log.info("새로운 상태 저장 완료. Status ID: {}", savedStatus.getStatusId());

        return savedStatus;
    }

    /**
     * Status 엔티티를 응답 DTO로 변환
     */
    private ChildStatusResponse convertToResponse(Status status, Organization organization) {
        return ChildStatusResponse.builder()
                .organizationId(organization.getOrganizationId())
                .childName(organization.getChildName())
                .statusId(status.getStatusId())
                .chatGptStatusDto(ChatGptStatusDto.fromStatus(status))
                .build();
    }
}