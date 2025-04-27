package com.ktnu.AiLectureSummary.repository;

import com.ktnu.AiLectureSummary.domain.Member;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class MemoryMemberRepositoryTest {

    MemoryMemberRepository repository = new MemoryMemberRepository();

    @AfterEach
    public void afterEach(){
        repository.clearStore();
    }

    @Test
    void save() {
        //given
        Member member = new Member();
        member.setName("testUser");

        //when
        repository.save(member);

        //then
        Optional<Member> result = repository.findById(member.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(member);
    }

    @Test
    void findById() {
        //given
        Member member1 = new Member();
        member1.setName("testUser1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("testUser2");
        repository.save(member2);

        //when
        Optional<Member> findMember = repository.findById(member2.getId());

        //then
        assertThat(findMember).isPresent();
        assertThat(findMember.get()).isEqualTo(member2);
    }

    @Test
    void findByName() {
        //given
        Member member1 = new Member();
        member1.setName("testUser1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("testUser2");
        repository.save(member2);

        //when
        Optional<Member> findMember = repository.findByName(member1.getName());

        //then
        assertThat(findMember).isPresent();
        assertThat(findMember.get()).isEqualTo(member1);
    }

    @Test
    void findAll() {
        Member member1 = new Member();
        member1.setName("testUser1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("testUser2");
        repository.save(member2);

        List<Member> members = repository.findAll();

        assertThat(members).hasSize(2);
        assertThat(members).containsExactlyInAnyOrder(member1, member2);

    }

}