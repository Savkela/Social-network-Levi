package com.levi9.socialnetwork.dto;

import com.levi9.socialnetwork.Model.Item;
import com.levi9.socialnetwork.Model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostDTO {

    private boolean isPrivate;
    private String text;
    private LocalDateTime createdDate;
    private Long groupId;
    private Set<User> hiddenFrom = new HashSet<>();
    private Set<Item> items = new HashSet<>();
}
