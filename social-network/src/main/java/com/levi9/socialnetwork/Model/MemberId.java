package com.levi9.socialnetwork.Model;

import java.io.Serializable;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MemberId implements Serializable {
    private Long idUser;
    private Long idGroup;

    public MemberId(Long idUser, Long idGroup) {
        this.idUser = idUser;
        this.idGroup = idGroup;
    }

    @Override
    public String toString() {
        return "idUser=" + idUser + ", idGroup=" + idGroup;
    }
}
