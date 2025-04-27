package com.ktnu.AiLectureSummary.repository;

import com.ktnu.AiLectureSummary.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    /****
 * Persists a Member entity and returns the saved instance.
 *
 * @param member the Member to be saved
 * @return the persisted Member entity
 */
Member save(Member member);
    /**
 * Retrieves a member by their unique identifier.
 *
 * @param id the unique identifier of the member
 * @return an Optional containing the member if found, or empty if no member exists with the given id
 */
Optional <Member> findById(long id);
    /**
 * Retrieves a member by their name.
 *
 * @param name the name of the member to search for
 * @return an Optional containing the found Member, or empty if no member with the given name exists
 */
Optional <Member> findByName(String name);
    /**
 * Retrieves all members from the repository.
 *
 * @return a list containing all Member objects
 */
List<Member> findAll ();
}
