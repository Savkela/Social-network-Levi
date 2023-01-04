package com.levi9.socialnetwork.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class GroupDTO {

    private boolean isPrivate;

    private String name;

    public GroupDTO(boolean isPrivate, String name) {
        this.isPrivate = isPrivate;
        this.name = name;
    }

}
