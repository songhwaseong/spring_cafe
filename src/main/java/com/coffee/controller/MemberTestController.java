package com.coffee.controller;
import com.coffee.entity.Member;
import com.coffee.repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController // 컨트롤러는 특정 요청에 대한 처리를 수행해 줍니다.
public class MemberTestController {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/api/insertMember")
    public ResponseEntity<?> createUser(@Valid @RequestBody Member member, BindingResult bindingResult){


        // 1) 유효성 검사 결과 확인
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        log.info("==========> {}",member);
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(member);

        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    @PostMapping("/api/updateMember")
    public Member modifyUser(@Valid @RequestBody Member member){
        log.info("==========> {}",member);
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(member);

        return member ;
    }
    @GetMapping("/api/memberList")
    public List<Member> getMemberList(){
        List<Member> mList =  memberRepository.findAll();
        log.info("==========> {}",mList);

        return mList;
    }
    @GetMapping("/api/lastMemberId")
    public Long getLastMemberId(){
        List<Member> mList =  memberRepository.findAll();
        Long lastMemberId = mList.stream().mapToLong(Member::getId).max().getAsLong();
        log.info("==========> {}",mList);

        return lastMemberId;
    }
}
