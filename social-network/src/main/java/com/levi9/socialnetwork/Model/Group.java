package com.levi9.socialnetwork.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "group", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "private")
    private boolean isPrivate;

    @Column(name = "id_admin")
    private Long idAdmin;

    @Column(name = "name")
    private String name;
    
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "request", joinColumns = @JoinColumn(name = "id_group"), inverseJoinColumns = @JoinColumn(name = "id_user"))
    @JsonIgnore
    private Collection<User> userRequests = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "member", joinColumns = @JoinColumn(name = "id_group"), inverseJoinColumns = @JoinColumn(name = "id_user"))
    private Set<User> members = new HashSet<>();

    public Group(boolean isPrivate, Long idAdmin, String name) {
        super();
        this.isPrivate = isPrivate;
        this.idAdmin = idAdmin;
        this.name = name;
    }

    public boolean containsUser(Long userId) {
        for (User user : getMembers()) {
            if (user.getId().equals(userId))
                return true;
        }
        return false;
    }

    public boolean containsUserRequest(Long userId) {
        for (User user : getUserRequests()) {
            if(user.getId().equals(userId))
                return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Group group = (Group) o;
        return id != null && Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
