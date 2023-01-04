package com.levi9.socialnetwork.dto;

import com.levi9.socialnetwork.Model.Item;
import com.levi9.socialnetwork.Model.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class PostDTO {

    private Long id;
    private boolean isPrivate;
    private String text;
    private LocalDateTime createdDate;
    private boolean deleted;
    private Long userId;
    private Long groupId;

    private Set<User> hiddenFrom = new HashSet<>();

    private Set<Item> items = new HashSet<>();

    public PostDTO(Long id, boolean isPrivate, String text, LocalDateTime createdDate, boolean deleted, Long userId,
            Long groupId, Set<User> hiddenFrom, Set<Item> items) {
        this.id = id;
        this.isPrivate = isPrivate;
        this.text = text;
        this.createdDate = createdDate;
        this.deleted = deleted;
        this.userId = userId;
        this.groupId = groupId;
    }

    public PostDTO() {
    }

    public PostDTO(boolean isPrivate, String text, LocalDateTime createdDate, boolean deleted, Long userId,
            Long groupId, Set<User> hiddenFrom, Set<Item> item) {
        this.isPrivate = isPrivate;
        this.text = text;
        this.createdDate = createdDate;
        this.deleted = deleted;
        this.userId = userId;
        this.groupId = groupId;
    }

}
