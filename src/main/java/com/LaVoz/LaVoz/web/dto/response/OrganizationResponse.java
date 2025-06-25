package com.LaVoz.LaVoz.web.dto.response;

import com.LaVoz.LaVoz.domain.Organization;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
public class OrganizationResponse {
    private Long organizationId;
    private String name;
    private String inviteCode;
    private List<NoteResponse> notes;

    @Builder
    public OrganizationResponse(Long organizationId, String name, String inviteCode, List<NoteResponse> notes) {
        this.organizationId = organizationId;
        this.name = name;
        this.inviteCode = inviteCode;
        this.notes = notes;
    }
    
    public static OrganizationResponse from(Organization organization) {
        return OrganizationResponse.builder()
                .organizationId(organization.getOrganizationId())
                .name(organization.getName())
                .inviteCode(organization.getInviteCode())
                .notes(Optional.ofNullable(organization.getNotes())
                    .map(notes -> notes.stream().map(NoteResponse::from).toList())
                    .orElse(List.of()))
                .build();
}
}