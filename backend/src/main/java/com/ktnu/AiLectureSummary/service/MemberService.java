package com.ktnu.AiLectureSummary.service;

import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.repository.MemberRepository;
import com.ktnu.AiLectureSummary.repository.MemoryMemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberService {

    private  final MemberRepository memberRepository;

    /**
     * Constructs a MemberService with the specified MemberRepository.
     *
     * @param memberRepository the repository used for member data operations
     */
    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    /**
     * Registers a new member after ensuring no duplicate name exists.
     *
     * @param member the member to register
     * @return the ID of the newly registered member
     * @throws IllegalStateException if a member with the same name already exists
     */
    public Long join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    /**
     * Checks if a member with the same name already exists and throws an exception if found.
     *
     * @param member the member to check for duplication
     * @throws IllegalStateException if a member with the same name already exists
     */
    private void validateDuplicateMember(Member member) {
        memberRepository.findByName(member.getName())
                .ifPresent(m ->{
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    /**
     * Retrieves a list of all members.
     *
     * @return a new list containing all members in the repository
     */
    public List<Member> findMembers(){
        return new ArrayList<>(memberRepository.findAll());
    }

    /**
     * Retrieves a member by their unique ID.
     *
     * @param memberId the ID of the member to retrieve
     * @return an {@code Optional} containing the member if found, or empty if not present
     */
    public Optional<Member> findOne(Long memberId){
        return memberRepository.findById(memberId);
    }
}
