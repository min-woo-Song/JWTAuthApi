package com.JWTAuthApi.demo.service;

import com.JWTAuthApi.demo.domain.Member;
import com.JWTAuthApi.demo.domain.ProviderType;
import com.JWTAuthApi.demo.domain.RoleType;
import com.JWTAuthApi.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다"));
    }

    @Transactional
    public Member saveMember(Member member) {
        member.setRoleType(RoleType.USER);
        member.setProviderType(ProviderType.LOCAL);
        return memberRepository.save(member);
    }

    @Transactional
    public Optional<Member> getMember(Long memberId){
        return memberRepository.findById(memberId);
    }
}
