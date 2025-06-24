package com.LaVoz.LaVoz.web.dto.response;

import com.LaVoz.LaVoz.domain.Status;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatGptStatusDto {
    private String morningEmotion;
    private String morningBehavior;
    private String afternoonEmotion;
    private String afternoonBehavior;
    private String nightEmotion;
    private String nightBehavior;
    private String happyBehaviorMap;
    private String sadBehaviorMap;
    private String annoyingBehaviorMap;
    private int hearingSensitivity;
    private int sightSensitivity;
    private int touchSensitivity;
    private int smellSensitivity;
    private int tasteSensitivity;
    private int socialSensitivity;

    public static ChatGptStatusDto fromStatus(Status status) {
        return ChatGptStatusDto.builder()
                .morningEmotion(status.getMorningEmotion())
                .morningBehavior(status.getMorningBehavior())
                .afternoonEmotion(status.getAfternoonEmotion())
                .afternoonBehavior(status.getAfternoonBehavior())
                .nightEmotion(status.getNightEmotion())
                .nightBehavior(status.getNightBehavior())
                .happyBehaviorMap(status.getHappyBehaviorMap())
                .sadBehaviorMap(status.getSadBehaviorMap())
                .annoyingBehaviorMap(status.getAnnoyingBehaviorMap())
                .hearingSensitivity(status.getHearingSensitivity())
                .sightSensitivity(status.getSightSensitivity())
                .touchSensitivity(status.getTouchSensitivity())
                .smellSensitivity(status.getSmellSensitivity())
                .tasteSensitivity(status.getTasteSensitivity())
                .socialSensitivity(status.getSocialSensitivity())
                .build();
    }
}
