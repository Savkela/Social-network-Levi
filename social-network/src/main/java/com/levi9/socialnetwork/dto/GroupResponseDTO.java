package com.levi9.socialnetwork.dto;

import com.levi9.socialnetwork.Model.Group;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GroupResponseDTO {

    private Long id;

    private Long idAdmin;

    private boolean isPrivate;

    private String name;

    public GroupResponseDTO(Long id, Long idAdmin, boolean isPrivate, String name) {
        this.id = id;
        this.idAdmin = idAdmin;
        this.isPrivate = isPrivate;
        this.name = name;
    }

    public GroupResponseDTO(Group group) {
        this.id = group.getId();
        this.idAdmin = group.getIdAdmin();
        this.isPrivate = group.isPrivate();
        this.name = group.getName();
    }

}
