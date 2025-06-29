package com.LaVoz.LaVoz.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildStatusResponse {
    private String childName;
    private Long statusId;
    private ChatGptStatusDto chatGptStatusDto;
    private Long issueId;
    private Long  memberId;
    private String memberName;
    private Long organizationId;
    private String organizationName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
