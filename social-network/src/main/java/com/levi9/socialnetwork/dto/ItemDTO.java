package com.levi9.socialnetwork.dto;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;

import com.levi9.socialnetwork.Model.Item;

@Data
@Builder
public class ItemDTO {

    public ItemDTO(Long id, String link) {
        this.id = id;
        this.link = link;
    }

    public ItemDTO( String link) {
        this.link = link;
    }

    public ItemDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    private Long id;
    private String link;
}
