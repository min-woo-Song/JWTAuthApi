package com.JWTAuthApi.demo.security.auth.service;

import com.JWTAuthApi.demo.domain.Member;
import com.JWTAuthApi.demo.domain.ProviderType;
import com.JWTAuthApi.demo.domain.RoleType;
import com.JWTAuthApi.demo.repository.MemberRepository;
import com.JWTAuthApi.demo.security.auth.exception.OAuthProviderMissMatchException;
import com.JWTAuthApi.demo.security.auth.userInfo.MemberPrincipal;
import com.JWTAuthApi.demo.security.auth.userInfo.OAuth2UserInfo;
import com.JWTAuthApi.demo.security.auth.userInfo.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User member = super.loadUser(userRequest);

        try {
            return this.process(userRequest, member);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User member) {

        // GOOGLE, KAKAO, NAVER, FACEBOOK 등 provider 타입을 가져온다
        ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration()
                .getRegistrationId().toUpperCase());

        // providerType, Attributes 이용하여 분류
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, member.getAttributes());
        Optional<Member> findMember = memberRepository.findByEmail(userInfo.getEmail());

        // 이미 가입되어 있고 ProviderType 이 다르면 업데이트
        if (findMember.isPresent()) {
            if (providerType != findMember.get().getProviderType()) {
                throw new OAuthProviderMissMatchException(
                        "Looks like you're signed up with " + providerType +
                                " account. Please use your " + findMember.get().getProviderType() + " account to login.");
            }
            updateMember(findMember.get(), userInfo);
        } else  // 가입되어있지 않은 회원이면 가입
            findMember = Optional.of(createMember(userInfo, providerType));

        Member principal = findMember.get();
        // OAuth2User 상속받은 MemberPrincipal 객체에 담아서 반환
        return new MemberPrincipal(
                principal.getEmail(),
                principal.getName(),
                null,
                principal.getProviderType(),
                Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getCode()))
                );
    }

    // 받은 데이터로 DB 저장
    private Member createMember(OAuth2UserInfo userInfo, ProviderType providerType) {
        Member member = new Member(
                userInfo.getEmail(),
                userInfo.getName(),
                null,
                providerType,
                RoleType.USER
        );
        return memberRepository.saveAndFlush(member);
    }

    private Member updateMember(Member member, OAuth2UserInfo userInfo) {
        if (userInfo.getName() != null && !member.getName().equals(userInfo.getName())) {
            member.setName(userInfo.getName());
        }
        return member;
    }
}
