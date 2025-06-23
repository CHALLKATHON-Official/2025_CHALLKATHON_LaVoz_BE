package com.LaVoz.LaVoz.web.dto.response;

import com.LaVoz.LaVoz.domain.Organization;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrganizationResponseDto {
    private Long organizationId;
    private String name;
    
    @Builder
    public OrganizationResponseDto(Long organizationId, String name, String description, String imageUrl) {
        this.organizationId = organizationId;
        this.name = name;
    }
    
    public static OrganizationResponseDto from(Organization organization) {
        return OrganizationResponseDto.builder()
            .organizationId(organization.getOrganizationId())
            .name(organization.getName())
            .build();
    }
}