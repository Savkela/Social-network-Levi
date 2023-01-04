package com.levi9.socialnetwork.Model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Role")
@Getter
@Setter
public class Role implements Serializable, GrantedAuthority {

    private static final long serialVersionUID = 1L;

    public Role() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Override
    public String getAuthority() {
        return name;
    }

}
