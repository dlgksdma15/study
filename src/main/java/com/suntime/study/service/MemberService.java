package com.suntime.study.service;

import com.suntime.study.dto.MemberDTO;
import com.suntime.study.entity.MemberEntity;
import com.suntime.study.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public boolean isEmailDuplicate(String email) {
        return memberRepository.findByMemberEmail(email).isPresent();
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }

    public void save(MemberDTO memberDTO) {
        String verificationToken = generateVerificationToken();

        // 비밀번호를 인코딩하여 회원가입
        String encodedPassword = passwordEncoder.encode(memberDTO.getMemberPW());
        memberDTO.setMemberPW(encodedPassword);

        // DTO를 Entity로 변환
        MemberEntity memberEntity = new MemberEntity(
                memberDTO.getMemberEmail(),
                memberDTO.getMemberPW(),
                memberDTO.getMemberName(),
                verificationToken
        );

        // Repository의 save 메서드 호출
        memberRepository.save(memberEntity);

        // 이메일 인증을 위해 인증 이메일 보내기
        emailService.sendVerificationEmail(memberDTO.getMemberEmail(), verificationToken);
    }

    public boolean verifyEmail(String memberEmail, String token) {
        MemberEntity member = memberRepository.findByMemberEmail(memberEmail).orElse(null);
        if (member != null && member.getVerificationToken().equals(token)) {
            member.setMemberEmailCheck(1);
            memberRepository.save(member);
            return true;
        }
        return false;
    }

    public MemberDTO login(MemberDTO memberDTO) {
        Optional<MemberEntity> optionalMember = memberRepository.findByMemberEmail(memberDTO.getMemberEmail());
        if (optionalMember.isPresent()) {
            MemberEntity memberEntity = optionalMember.get();
            if (passwordEncoder.matches(memberDTO.getMemberPW(), memberEntity.getMemberPW())) {
                if (memberEntity.getMemberEmailCheck() == 1) {
                    return MemberDTO.toMemberDTO(memberEntity);
                } else {
                    return null; // 이메일 체크가 되어있지 않은 회원일 경우 로그인 거부
                }
            } else {
                return null; // 비밀번호 불일치
            }
        } else {
            return null; // 해당 이메일을 가진 회원이 없음
        }
    }

    public MemberEntity findByEmail(String email) {
        Optional<MemberEntity> memberOptional = memberRepository.findByMemberEmail(email);
        return memberOptional.orElse(null);
    }
}
