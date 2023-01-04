package com.levi9.socialnetwork.Model;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class MuteGroupId implements Serializable {
    private Long userId;
    private Long groupId;

    public MuteGroupId() {
    }

    public MuteGroupId(Long userId, Long groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "userId=" + userId + ", groupId=" + groupId;
    }
}
