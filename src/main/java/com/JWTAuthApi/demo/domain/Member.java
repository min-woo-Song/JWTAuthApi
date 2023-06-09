package com.JWTAuthApi.demo.domain;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name="member")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@DynamicUpdate
public class Member {

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column
    private String email;

    @Column(length = 50)
    private String name;

    @Column(length = 500)
    private String password;

    @Column
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Column
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    public Member(
            String email,
            String name,
            String password,
            ProviderType providerType,
            RoleType roleType
    ) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.providerType = providerType;
        this.roleType = roleType;
    }
}
