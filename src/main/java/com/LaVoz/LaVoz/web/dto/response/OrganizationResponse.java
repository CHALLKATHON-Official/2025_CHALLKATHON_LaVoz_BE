package com.LaVoz.LaVoz.web.dto.response;

import com.LaVoz.LaVoz.domain.Note;
import com.LaVoz.LaVoz.domain.Organization;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrganizationResponse {
    private Long organizationId;
    private String name;
    private List<NoteResponse> notes;

    @Builder
    public OrganizationResponse(Long organizationId, String name, List<NoteResponse> notes) {
        this.organizationId = organizationId;
        this.name = name;
        this.notes = notes;
    }
    
    public static OrganizationResponse from(Organization organization) {
        return OrganizationResponse.builder()
                .organizationId(organization.getOrganizationId())
                .name(organization.getName())
                .notes(organization.getNotes().stream().map(NoteResponse::from).toList())
                .build();
    }
}