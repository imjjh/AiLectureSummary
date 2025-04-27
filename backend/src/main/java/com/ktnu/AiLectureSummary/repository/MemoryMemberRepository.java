package com.ktnu.AiLectureSummary.repository;

import com.ktnu.AiLectureSummary.domain.Member;

import java.util.*;

/**
* 동시성 문제가 고려되어 있지 않음, 실무에서는 ConcurrentHashMap, AtomicLong 사용 고려
 */
 public class MemoryMemberRepository implements MemberRepository{

    private static Map<Long, Member> store = new HashMap<>();
    private static Long sequence = 0L;

    /**
     * Saves a member to the in-memory store, assigning it a unique ID.
     *
     * @param member the member to be saved
     * @return the saved member with an assigned unique ID
     */
    @Override
    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    /**
     * Retrieves a member by its unique ID.
     *
     * @param id the unique identifier of the member
     * @return an {@code Optional} containing the member if found, or empty if no member exists with the given ID
     */
    @Override
    public Optional<Member> findById(long id) {
        return Optional.ofNullable(store.get(id));
    }

    /**
     * Searches for a member with the specified name.
     *
     * @param name the name of the member to search for
     * @return an {@code Optional} containing the found member, or empty if no member with the given name exists
     */
    @Override
    public Optional<Member> findByName(String name) {
        return store.values().stream()
                .filter(member -> member.getName().equals(name))
                .findAny();
    }

    /**
     * Returns a list of all members currently stored in memory.
     *
     * @return a list containing all stored Member objects
     */
    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * Removes all members from the in-memory store.
     *
     * This operation deletes all stored members and resets the repository to an empty state.
     */
    public void clearStore(){
        store.clear();
    }
}
