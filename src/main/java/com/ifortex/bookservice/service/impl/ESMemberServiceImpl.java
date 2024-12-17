package com.ifortex.bookservice.service.impl;

import com.ifortex.bookservice.model.Member;
import com.ifortex.bookservice.repository.impl.MemberRepositoryImpl;
import com.ifortex.bookservice.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// Attention! It is FORBIDDEN to make any changes in this file!
@Service
public class ESMemberServiceImpl implements MemberService {
    @Autowired
    private MemberRepositoryImpl memberRepository;

    @Override
    public Member findMember() {
        return memberRepository.findMemberByRomance();
    }

    @Override
    public List<Member> findMembers() {
        // will be implemented shortly
      return memberRepository.findMembers();
    }
}
