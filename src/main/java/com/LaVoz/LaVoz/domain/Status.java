package com.LaVoz.LaVoz.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Status extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statusId;

    private String morningEmotion;

    private String morningBehavior;

    private String afternoonEmotion;

    private String afternoonBehavior;

    private String nightEmotion;

    private String nightBehavior;

    private String happyBehaviorMap;

    private String sadBehaviorMap;

    private String annoyingBehaviorMap;

    private int hearingSensitivity; // 청각

    private int sightSensitivity; // 시각

    private int touchSensitivity; // 촉각

    private int smellSensitivity; // 후각

    private int tasteSensitivity; // 미각

    private int socialSensitivity; // 사회적 자극

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
}
