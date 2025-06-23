package com.LaVoz.LaVoz.service;

import com.LaVoz.LaVoz.domain.Member;
import com.LaVoz.LaVoz.domain.MemberOrganization;
import com.LaVoz.LaVoz.domain.Organization;
import com.LaVoz.LaVoz.repository.MemberOrganizationRepository;
import com.LaVoz.LaVoz.repository.MemberRepository;
import com.LaVoz.LaVoz.repository.OrganizationRepository;
import com.LaVoz.LaVoz.web.dto.response.OrganizationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private MemberOrganizationRepository memberOrganizationRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private OrganizationService organizationService;

    private Member testMember;
    private Organization testOrganization;
    private MemberOrganization testMemberOrganization;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        testMember = Member.builder()
                .memberId(1L)
                .email("test@example.com")
                .name("테스트 사용자")
                .build();

        testOrganization = Organization.builder()
                .organizationId(1L)
                .name("테스트 조직")
                .build();

        testMemberOrganization = MemberOrganization.builder()
                .memberOrganizationId(1L)
                .member(testMember)
                .organization(testOrganization)
                .build();
    }

    @Test
    @DisplayName("회원 ID로 조직 목록 조회 성공")
    void findOrganizationsByMemberId_Success() {
        // given
        Long memberId = 1L;
        when(memberOrganizationRepository.findByMember_MemberId(memberId))
                .thenReturn(Arrays.asList(testMemberOrganization));

        // when
        List<OrganizationResponse> result = organizationService.findOrganizationsByMemberId(memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getOrganizationId()).isEqualTo(testOrganization.getOrganizationId());
        assertThat(result.get(0).getName()).isEqualTo(testOrganization.getName());

        verify(memberOrganizationRepository, times(1)).findByMember_MemberId(memberId);
    }

    @Test
    @DisplayName("조직 생성 성공")
    void createOrganization_Success() {
        // given
        String orgName = "새 조직";
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
        when(organizationRepository.save(any(Organization.class))).thenReturn(
                Organization.builder()
                        .organizationId(2L)
                        .name(orgName)
                        .build()
        );

        // when
        OrganizationResponse result = organizationService.createOrganization(orgName, memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(orgName);
        assertThat(result.getOrganizationId()).isEqualTo(2L);

        verify(memberRepository, times(1)).findById(memberId);
        verify(organizationRepository, times(1)).save(any(Organization.class));
        verify(memberOrganizationRepository, times(1)).save(any(MemberOrganization.class));
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 조직 생성 시 예외 발생")
    void createOrganization_MemberNotFound() {
        // given
        String orgName = "새 조직";
        Long memberId = 999L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            organizationService.createOrganization(orgName, memberId);
        });

        assertThat(exception.getMessage()).contains("Member not found");
        verify(memberRepository, times(1)).findById(memberId);
        verify(organizationRepository, never()).save(any(Organization.class));
    }

    @Test
    @DisplayName("조직 삭제 성공")
    void deleteOrganization_Success() {
        // given
        Long organizationId = 1L;
        Long memberId = 1L;

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(testOrganization));
        when(memberOrganizationRepository.existsByMember_MemberIdAndOrganization_OrganizationId(memberId, organizationId))
                .thenReturn(true);
        doNothing().when(organizationRepository).delete(any(Organization.class));

        // when
        boolean result = organizationService.deleteOrganization(organizationId, memberId);

        // then
        assertThat(result).isTrue();
        verify(organizationRepository, times(1)).findById(organizationId);
        verify(memberOrganizationRepository, times(1))
                .existsByMember_MemberIdAndOrganization_OrganizationId(memberId, organizationId);
        verify(organizationRepository, times(1)).delete(testOrganization);
    }

    @Test
    @DisplayName("존재하지 않는 조직 삭제 시 예외 발생")
    void deleteOrganization_OrganizationNotFound() {
        // given
        Long organizationId = 999L;
        Long memberId = 1L;

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            organizationService.deleteOrganization(organizationId, memberId);
        });

        assertThat(exception.getMessage()).contains("Organization not found");
        verify(organizationRepository, times(1)).findById(organizationId);
        verify(memberOrganizationRepository, never())
                .existsByMember_MemberIdAndOrganization_OrganizationId(any(), any());
    }

    @Test
    @DisplayName("조직에 속하지 않은 회원이 삭제 시도 시 예외 발생")
    void deleteOrganization_MemberNotInOrganization() {
        // given
        Long organizationId = 1L;
        Long memberId = 2L;

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(testOrganization));
        when(memberOrganizationRepository.existsByMember_MemberIdAndOrganization_OrganizationId(memberId, organizationId))
                .thenReturn(false);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            organizationService.deleteOrganization(organizationId, memberId);
        });

        assertThat(exception.getMessage()).contains("does not belong to Organization");
        verify(organizationRepository, times(1)).findById(organizationId);
        verify(memberOrganizationRepository, times(1))
                .existsByMember_MemberIdAndOrganization_OrganizationId(memberId, organizationId);
        verify(organizationRepository, never()).delete(any());
    }

    @Test
    @DisplayName("조직에 새 멤버 추가 성공")
    void addMemberToOrganization_Success() {
        // given
        Long organizationId = 1L;
        Long memberId = 2L;
        Long requesterId = 1L;

        Member newMember = Member.builder()
                .memberId(memberId)
                .email("newmember@example.com")
                .name("새 멤버")
                .build();

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(testOrganization));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(newMember));

        // 요청자가 조직에 속함
        when(memberOrganizationRepository.existsByMember_MemberIdAndOrganization_OrganizationId(
                requesterId, organizationId)).thenReturn(true);

        // 추가할 멤버는 아직 조직에 속하지 않음
        when(memberOrganizationRepository.existsByMember_MemberIdAndOrganization_OrganizationId(
                memberId, organizationId)).thenReturn(false);

        // when
        boolean result = organizationService.addMemberToOrganization(organizationId, memberId, requesterId);

        // then
        assertThat(result).isTrue();
        verify(organizationRepository, times(1)).findById(organizationId);
        verify(memberRepository, times(1)).findById(memberId);
        verify(memberOrganizationRepository, times(1))
                .existsByMember_MemberIdAndOrganization_OrganizationId(requesterId, organizationId);
        verify(memberOrganizationRepository, times(1))
                .existsByMember_MemberIdAndOrganization_OrganizationId(memberId, organizationId);
        verify(memberOrganizationRepository, times(1)).save(any(MemberOrganization.class));
    }

    @Test
    @DisplayName("존재하지 않는 조직에 멤버 추가 시 예외 발생")
    void addMemberToOrganization_OrganizationNotFound() {
        // given
        Long organizationId = 999L;
        Long memberId = 2L;
        Long requesterId = 1L;

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            organizationService.addMemberToOrganization(organizationId, memberId, requesterId);
        });

        assertThat(exception.getMessage()).contains("Organization not found");
        verify(organizationRepository, times(1)).findById(organizationId);
        verify(memberRepository, never()).findById(any());
        verify(memberOrganizationRepository, never()).save(any());
    }

    @Test
    @DisplayName("요청자가 조직에 속하지 않은 경우 예외 발생")
    void addMemberToOrganization_RequesterNotInOrganization() {
        // given
        Long organizationId = 1L;
        Long memberId = 2L;
        Long requesterId = 3L;

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(testOrganization));

        // 요청자가 조직에 속하지 않음
        when(memberOrganizationRepository.existsByMember_MemberIdAndOrganization_OrganizationId(
                requesterId, organizationId)).thenReturn(false);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            organizationService.addMemberToOrganization(organizationId, memberId, requesterId);
        });

        assertThat(exception.getMessage()).contains("does not belong to Organization");
        verify(organizationRepository, times(1)).findById(organizationId);
        verify(memberOrganizationRepository, times(1))
                .existsByMember_MemberIdAndOrganization_OrganizationId(requesterId, organizationId);
        verify(memberRepository, never()).findById(any());
        verify(memberOrganizationRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 멤버 추가 시 예외 발생")
    void addMemberToOrganization_MemberNotFound() {
        // given
        Long organizationId = 1L;
        Long memberId = 999L;
        Long requesterId = 1L;

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(testOrganization));

        // 요청자가 조직에 속함
        when(memberOrganizationRepository.existsByMember_MemberIdAndOrganization_OrganizationId(
                requesterId, organizationId)).thenReturn(true);

        // 추가할 멤버가 존재하지 않음
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            organizationService.addMemberToOrganization(organizationId, memberId, requesterId);
        });

        assertThat(exception.getMessage()).contains("Member not found");
        verify(organizationRepository, times(1)).findById(organizationId);
        verify(memberOrganizationRepository, times(1))
                .existsByMember_MemberIdAndOrganization_OrganizationId(requesterId, organizationId);
        verify(memberRepository, times(1)).findById(memberId);
        verify(memberOrganizationRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미 조직에 속한 멤버 추가 시 예외 발생")
    void addMemberToOrganization_MemberAlreadyInOrganization() {
        // given
        Long organizationId = 1L;
        Long memberId = 2L;
        Long requesterId = 1L;

        Member newMember = Member.builder()
                .memberId(memberId)
                .email("newmember@example.com")
                .name("새 멤버")
                .build();

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(testOrganization));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(newMember));

        // 요청자가 조직에 속함
        when(memberOrganizationRepository.existsByMember_MemberIdAndOrganization_OrganizationId(
                requesterId, organizationId)).thenReturn(true);

        // 추가할 멤버가 이미 조직에 속함
        when(memberOrganizationRepository.existsByMember_MemberIdAndOrganization_OrganizationId(
                memberId, organizationId)).thenReturn(true);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            organizationService.addMemberToOrganization(organizationId, memberId, requesterId);
        });

        assertThat(exception.getMessage()).contains("already belongs to Organization");
        verify(organizationRepository, times(1)).findById(organizationId);
        verify(memberOrganizationRepository, times(1))
                .existsByMember_MemberIdAndOrganization_OrganizationId(requesterId, organizationId);
        verify(memberRepository, times(1)).findById(memberId);
        verify(memberOrganizationRepository, times(1))
                .existsByMember_MemberIdAndOrganization_OrganizationId(memberId, organizationId);
        verify(memberOrganizationRepository, never()).save(any());
    }
}