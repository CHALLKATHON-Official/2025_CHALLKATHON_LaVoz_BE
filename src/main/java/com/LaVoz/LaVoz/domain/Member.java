package com.LaVoz.LaVoz.domain;

import com.LaVoz.LaVoz.domain.enums.Role;
import com.LaVoz.LaVoz.web.dto.request.MemberUpdateRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private Role role;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String loginId;
    private String email;

    private String childName;

    private String childGender;

    private String childBirthday;

    private String childImageUrl;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Note> notes = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Issue> issues = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MemberOrganization> userOrganizations = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardComment> boardComments = new ArrayList<>();

    public void setEncodedPassword(String password) {
        this.password = password;
    }

    public void updateMember(MemberUpdateRequest request) {
        this.name = request.getName();
        this.imageUrl = request.getImageUrl();
        this.childName = request.getChildName();
        this.childGender = request.getChildGender();
        this.childBirthday = request.getChildBirthday();
        this.childImageUrl = request.getChildImageUrl();
    }
}
