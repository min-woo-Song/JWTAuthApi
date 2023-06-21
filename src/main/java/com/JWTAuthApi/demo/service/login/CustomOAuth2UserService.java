package com.JWTAuthApi.demo.service.login;

import com.JWTAuthApi.demo.domain.ProviderType;
import com.JWTAuthApi.demo.domain.RoleType;
import com.JWTAuthApi.demo.domain.User;
import com.JWTAuthApi.demo.mapper.UserRepository;
import com.JWTAuthApi.demo.security.auth.exception.OAuthProviderMissMatchException;
import com.JWTAuthApi.demo.security.auth.userInfo.UserPrincipal;
import com.JWTAuthApi.demo.security.auth.userInfo.OAuth2UserInfo;
import com.JWTAuthApi.demo.security.auth.userInfo.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return this.process(userRequest, oAuth2User);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
            // 시스템 문제로 내부 인증관련 처리 요청을 할 수 없는 경우
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {

        // GOOGLE, KAKAO, NAVER, FACEBOOK 등 provider 타입을 가져온다
        ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration()
                .getRegistrationId().toUpperCase());

        // providerType, Attributes로 분류하여 user 정보를 가져온다
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, oAuth2User.getAttributes());

        User user = userRepository.findByEmail(userInfo.getEmail());

        // 이미 가입되어 있고 ProviderType 이 다르면 업데이트
        if (user != null) {
            if (providerType != user.getProviderType()) {
                throw new OAuthProviderMissMatchException(
                        "you're signed up with " + providerType +
                                " account. Please use your " + user.getProviderType() + " account to login.");
            }
            user = updateMember(user, userInfo);
        } else  // 가입되어있지 않은 회원이면 가입
            user = createUser(userInfo, providerType);

        // OAuth2User 상속받은 MemberPrincipal 객체에 담아서 반환
        return new UserPrincipal(
                user.getUserId(),
                user.getEmail(),
                user.getUsername(),
                user.getProviderType(),
                Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getCode()))
                );
    }

    // 받은 데이터로 DB 저장
    private User createUser(OAuth2UserInfo userInfo, ProviderType providerType) {
        User user = User.builder()
                .email(userInfo.getEmail())
                .username(userInfo.getName())
                .password(passwordEncoder.encode(providerType.name() + userInfo.getEmail()))
                .providerType(providerType)
                .roleType(RoleType.USER)
                .build();
        userRepository.saveUser(user);
        return userRepository.findByEmail(user.getEmail());
    }

    private User updateMember(User user, OAuth2UserInfo userInfo) {
        if (userInfo.getName() != null && !user.getUsername().equals(userInfo.getName())) {
            userRepository.updateUser(user.getUserId(), userInfo.getName());
            user = userRepository.findByEmail(user.getEmail());
        }
        return user;
    }
}
