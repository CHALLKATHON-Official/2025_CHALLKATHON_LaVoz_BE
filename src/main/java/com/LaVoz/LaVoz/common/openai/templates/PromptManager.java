package com.LaVoz.LaVoz.common.openai.templates;

import com.LaVoz.LaVoz.domain.Note;
import com.LaVoz.LaVoz.domain.Status;
import com.LaVoz.LaVoz.web.dto.response.NoteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PromptTemplate에 맞게 프롬프트를 생성, 관리하는 클래스
 */
@Slf4j
@Component
public class PromptManager {

    /**
     * 이슈에 대한 답변 프롬프트 생성
     */
    public String createIssuePrompt(String question, Status currentStatus, List<NoteResponse> notes) {
        // 기존 상태 데이터 문자열 생성
        String existingStatusData = buildExistingStatusData(currentStatus);

        String similarNotesData = buildNoteResponsesData(notes);
        log.debug("similarNotesData: {}", similarNotesData);

        PromptTemplate promptTemplate = new PromptTemplate();
        return promptTemplate.fillTemplate(
                """
                ## 명령
                다음은 자폐 스펙트럼 특성을 가진 아이의 상태 정보와 행동 기록입니다. 아래 내용을 참고하여 보호자가 보낸 질문에 성실하고 구체적으로 답변해주세요.
                
                - 아이의 상태 정보와 행동 기록들은 질문에 답변하기 위한 참고 자료입니다. 이 데이터들을 분석은 하되 분석한 내용이나 요약한 내용을 답변에 포함하지 말고, 오직 질문에 대한 답변을 구성하는 데만 활용해주세요.
                - 제공된 행동 기록 중 일부는 보호자의 질문과 직접적인 관련이 없을 수 있습니다. 질문의 맥락에 맞는 정보만 선별하여 답변에 반영해주세요.
                - 질문에 집중하여 보호자가 원하는 해석이나 조언을 직접적으로 제공해주세요.
                - 보호자가 쉽게 이해할 수 있도록 일상적인 한국어 문장으로 설명해주세요.
                - GPT나 인공지능이라는 표현은 사용하지 마세요.
                - 응답은 분석과 조언 중심으로, 구체적인 원인 설명과 대응 방법을 포함해주세요.
                - 명확하지 않은 부분은 "추가 관찰이 필요합니다" 또는 "개별 차이를 고려해야 합니다" 등의 표현을 사용해주세요.

                ## 아이의 상태 정보
                %s
                
                ## 아이의 행동 기록
                %s
                
                ## 보호자의 질문
                %s
                """.formatted(existingStatusData, similarNotesData, question),
                """
                보호자의 질문에 대해 아이의 특성과 상태 정보를 반영하여, 차분하고 진정성 있는 문장으로 답변해주세요.
                """
        );
    }

    /**
     * 아이 상태 갱신을 위한 프롬프트 생성
     */
    public String createChildStateAnalysisPrompt(Status currentStatus, List<Note> notes) {
        // 기존 상태 데이터 문자열 생성
        String existingStatusData = buildExistingStatusData(currentStatus);

        // 새로운 노트들을 문자열로 변환
        String newNotesData = buildNotesData(notes);

        PromptTemplate promptTemplate = new PromptTemplate();
        return promptTemplate.fillTemplate(
                        """
                        ## 명령
                        다음은 자폐 스펙트럼 특성을 가진 아이에 대한 상태 분석 요청입니다.
                        기존 상태 데이터와 새로운 행동 노트를 바탕으로 아이의 다음 상태 데이터를 JSON 형식으로 생성하거나 갱신해주세요.
                        
                        분석 항목:
                        1. 시간별 감정과 주된 행동
                        2. 감정별 반복 행동
                        3. 감각 자극에 대한 민감도
                        
                        작성 시 유의사항:
                        - 민감도는 1~5 사이의 정수로 표현하며, 1은 '매우 둔감', 5는 '매우 민감'을 의미합니다.
                        - 행동은 구체적이고 관찰 가능한 표현을 사용해주세요.
                        - 자폐 스펙트럼 특성을 고려해 행동을 해석해주세요.
                        - 반드시 아래의 응답값 형식에 맞춘 JSON 형식으로만 응답해주세요. 다른 설명이나 말머리는 포함하지 마세요.
                        
                        ## 기존 아이 상태 데이터
                        %s
                        
                        ## 새로운 행동 노트들
                        %s
                        """.formatted(existingStatusData, newNotesData),
                """
                        {
                          "morningEmotion": "아침에 빈번하게 발생하는 감정",
                          "morningBehavior": "아침에 주로 보이는 행동들 (콤마로 구분)",
                          "afternoonEmotion": "오후에 빈번하게 발생하는 감정",
                          "afternoonBehavior": "오후에 주로 보이는 행동들 (콤마로 구분)",
                          "nightEmotion": "저녁에 빈번하게 발생하는 감정",
                          "nightBehavior": "저녁에 주로 보이는 행동들 (콤마로 구분)",
                          "happyBehaviorMap": "기쁠 때 나타나는 행동들 (콤마로 구분)",
                          "sadBehaviorMap": "슬플 때 나타나는 행동들 (콤마로 구분)",
                          "annoyingBehaviorMap": "짜증날 때 나타나는 행동들 (콤마로 구분)",
                          "hearingSensitivity": 1~5 사이 정수,
                          "sightSensitivity": 1~5 사이 정수,
                          "touchSensitivity": 1~5 사이 정수,
                          "smellSensitivity": 1~5 사이 정수,
                          "tasteSensitivity": 1~5 사이 정수,
                          "socialSensitivity": 1~5 사이 정수
                        }
                        """
        );
    }

    /**
     * 기존 상태 데이터를 문자열로 변환
     */
    private String buildExistingStatusData(Status currentStatus) {
        if (currentStatus == null) {
            return "기존 상태 데이터가 없습니다. 새로운 노트들을 기반으로 상태를 생성해주세요.";
        }

        StringBuilder statusBuilder = new StringBuilder();
        statusBuilder.append("=== 기존 아이 상태 데이터 ===\n");

        // 시간별 감정/행동
        statusBuilder.append("【시간별 감정/행동】\n");
        statusBuilder.append("- 아침 감정: ").append(currentStatus.getMorningEmotion()).append("\n");
        statusBuilder.append("- 아침 행동: ").append(currentStatus.getMorningBehavior()).append("\n");
        statusBuilder.append("- 오후 감정: ").append(currentStatus.getAfternoonEmotion()).append("\n");
        statusBuilder.append("- 오후 행동: ").append(currentStatus.getAfternoonBehavior()).append("\n");
        statusBuilder.append("- 저녁 감정: ").append(currentStatus.getNightEmotion()).append("\n");
        statusBuilder.append("- 저녁 행동: ").append(currentStatus.getNightBehavior()).append("\n\n");

        // 감정별 행동 맵
        statusBuilder.append("【감정별 행동 패턴】\n");
        statusBuilder.append("- 기쁠 때: ").append(currentStatus.getHappyBehaviorMap()).append("\n");
        statusBuilder.append("- 슬플 때: ").append(currentStatus.getSadBehaviorMap()).append("\n");
        statusBuilder.append("- 짜증날 때: ").append(currentStatus.getAnnoyingBehaviorMap()).append("\n\n");

        // 민감도 프로필
        statusBuilder.append("【감각 민감도】\n");
        statusBuilder.append("- 청각 민감도: ").append(currentStatus.getHearingSensitivity()).append("/5\n");
        statusBuilder.append("- 시각 민감도: ").append(currentStatus.getSightSensitivity()).append("/5\n");
        statusBuilder.append("- 촉각 민감도: ").append(currentStatus.getTouchSensitivity()).append("/5\n");
        statusBuilder.append("- 후각 민감도: ").append(currentStatus.getSmellSensitivity()).append("/5\n");
        statusBuilder.append("- 미각 민감도: ").append(currentStatus.getTasteSensitivity()).append("/5\n");
        statusBuilder.append("- 사회적 자극 민감도: ").append(currentStatus.getSocialSensitivity()).append("/5\n");

        return statusBuilder.toString();
    }


    /**
     * 노트들을 문자열로 변환
     */
    private String buildNotesData(List<Note> notes) {
        if (notes == null || notes.isEmpty()) {
            return "노트가 없습니다.";
        }

        StringBuilder notesBuilder = new StringBuilder();
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            notesBuilder.append(String.format(
                    "노트 %d:\n제목: %s\n내용: %s\n감정: %s\n시간대: %s\n\n",
                    i + 1,
                    note.getTitle() != null ? note.getTitle() : "제목 없음",
                    note.getContent() != null ? note.getContent() : "내용 없음",
                    note.getEmotion() != null ? note.getEmotion() : "감정 정보 없음",
                    note.getTime() != null ? note.getTime() : "시간대 정보 없음"
            ));
        }

        return notesBuilder.toString().trim(); // 마지막 개행 제거
    }

    private String buildNoteResponsesData(List<NoteResponse> notes) {
        if (notes == null || notes.isEmpty()) {
            return "노트가 없습니다.";
        }

        StringBuilder notesBuilder = new StringBuilder();
        for (int i = 0; i < notes.size(); i++) {
            NoteResponse note = notes.get(i);
            notesBuilder.append(String.format(
                    "노트 %d:\n제목: %s\n내용: %s\n감정: %s\n시간대: %s\n\n",
                    i + 1,
                    note.getTitle() != null ? note.getTitle() : "제목 없음",
                    note.getContent() != null ? note.getContent() : "내용 없음",
                    note.getEmotion() != null ? note.getEmotion() : "감정 정보 없음",
                    note.getTime() != null ? note.getTime() : "시간대 정보 없음"
            ));
        }

        return notesBuilder.toString().trim(); // 마지막 개행 제거
    }
}
