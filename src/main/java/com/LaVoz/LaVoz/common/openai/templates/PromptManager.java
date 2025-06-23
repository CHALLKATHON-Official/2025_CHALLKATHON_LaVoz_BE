package com.LaVoz.LaVoz.common.openai.templates;

import com.LaVoz.LaVoz.domain.Note;
import com.LaVoz.LaVoz.domain.Status;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PromptTemplate에 맞게 프롬프트를 생성, 관리하는 클래스
 */
@Component
public class PromptManager {

    /**
     * 이슈에 대한 답변 프롬프트 생성
     */

    /**
     * 아이 상태 갱신을 위한 프롬프트 생성
     */
    public String createChildStateAnalysisPrompt(Status currentStatus, List<Note> notes) {
        // 기존 상태 데이터 문자열 생성
        String existingStatusData = buildExistingStatusData(currentStatus);

        // 새로운 노트들을 문자열로 변환
        String newNotesData = buildNewNotesData(notes);

        PromptTemplate promptTemplate = new PromptTemplate();
        return promptTemplate.fillTemplate(
                        """
                        ## 명령
                        기존 데이터와 새로운 아이의 행동 노트를 보고 아이의 다음 상태 데이터를 생성 혹은 갱신해줘
                        1. 시간별 빈번한 행동과 감정(time_emotion_behavior)
                        2. 특정 감정일 때 자주 발생한 행동(emotion_behavior_map)
                        3. 청각, 시각, 촉각, 후각, 미각, 사회적 자극에 따른 민감 정도를 1~5로 나타내줘(sensitivity_profile)
                        - 민감도는 1=매우 둔감, 5=매우 민감으로 평가
                        - 행동은 구체적이고 관찰 가능한 것으로 작성
                        - 오직 JSON 형식으로만 응답하고 다른 설명은 포함하지 마세요
                        
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
     * 새로운 노트들을 문자열로 변환
     */
    private String buildNewNotesData(List<Note> notes) {
        if (notes == null || notes.isEmpty()) {
            return "새로운 노트가 없습니다.";
        }

        StringBuilder notesBuilder = new StringBuilder();
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            notesBuilder.append(String.format(
                    "노트 %d:\n제목: %s\n내용: %s\n감정: %s\n\n",
                    i + 1,
                    note.getTitle() != null ? note.getTitle() : "제목 없음",
                    note.getContent() != null ? note.getContent() : "내용 없음",
                    note.getEmotion() != null ? note.getEmotion() : "감정 정보 없음"
            ));
        }

        return notesBuilder.toString().trim(); // 마지막 개행 제거
    }
}
