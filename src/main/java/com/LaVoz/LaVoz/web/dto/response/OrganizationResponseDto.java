package com.LaVoz.LaVoz.web.dto.response;

import com.LaVoz.LaVoz.domain.Note;
import com.LaVoz.LaVoz.domain.Organization;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrganizationResponseDto {
    private Long organizationId;
    private String name;
    private List<Note> notes;
    
    @Builder
    public OrganizationResponseDto(Long organizationId, String name, List<Note> notes) {
        this.organizationId = organizationId;
        this.name = name;
        this.notes = notes;
    }
    
    public static OrganizationResponseDto from(Organization organization) {
        return OrganizationResponseDto.builder()
                .organizationId(organization.getOrganizationId())
                .name(organization.getName())
                .notes(organization.getNotes())
                .build();
    }
}