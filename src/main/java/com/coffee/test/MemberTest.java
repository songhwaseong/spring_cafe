package com.coffee.test;

import com.coffee.constant.Role;
import com.coffee.entity.Member;
import com.coffee.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestConstructor;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class MemberTest {


    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 추가하기")
    void insertMember() {

        memberRepository.save(Member.builder()
                .name("관리자")
                .email("admin1@naver.com")
                .password(passwordEncoder.encode("user1@123"))
                .address("서울어딘가")
                .role(Role.ADMIN)
                .build());

    }

    @Test
    @DisplayName("회원목록 조회하기")
    void selectMemberList() {
        List<Member> mem =  memberRepository.findAll();
        for(Member member : mem){
            System.out.println(passwordEncoder.matches("1q2w3e!@22", member.getPassword()));
        }

        log.info("===============> {}",mem);
    }

    @Test
    @DisplayName("회원 업데이트 하기")
    void updateMember() {
        memberRepository.save(Member.builder()
                .id(102L)
                .name("관리자2222")
                .email("adminss111@naver.com")
                .password(passwordEncoder.encode("user11111@123"))
                .address("서울어딘가1111")
                .role(Role.ADMIN)
                .build());

    }

    @Test
    @DisplayName("회원 삭제하기")
    void deleteMember() {
        memberRepository.deleteById(102L);

    }
}
