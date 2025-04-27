package com.ktnu.AiLectureSummary.domain;

public class Member {
    private Long id;
    private String name;

    /**
     * Returns the unique identifier of the member.
     *
     * @return the member's ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this member.
     *
     * @param id the unique identifier to assign
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name of the member.
     *
     * @return the member's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the member.
     *
     * @param name the name to assign to the member
     */
    public void setName(String name) {
        this.name = name;
    }
}
