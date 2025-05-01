package com.ktnu.AiLectureSummary.Member.infrastructure;

import com.ktnu.AiLectureSummary.Member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA 방식
 *
 * Member 엔티티를 위한 Repository 인터페이스
 * spring data jpa가 자동으로 구현체 생성
 *
 * JpaRepository에서 다음과 같은 CRUD 메서드가 자동 제공됨.
 * save(), findById(), findAll(), deleteById(), count() 등
 * 기본 키(PK)에 해당하는 필드(id)는 findById()와 같은 메서드를 별도 선언 없이 사용 가능하다.
 *
 * 추가로 아래처럼 선언만 하면 쿼리 메서드도 자동 생성됨:
 *  - findByEmail()
 *  - findByUsername()
 * ※ 메서드명에서 필드명 부분(Email, Username)은 대소문자를 구분하지 않는다.
 *
 * 복잡한 조건이 필요한 경우에는 @Query를 이용해 JPQL을 직접 작성할 수 있다.
 */
public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional <Member> findByEmail(String email);
    Optional <Member> findByUsername(String username);
}
