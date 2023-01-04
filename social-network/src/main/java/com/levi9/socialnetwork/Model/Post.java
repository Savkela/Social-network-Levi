package com.levi9.socialnetwork.Model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "post", schema = "public")
@Getter
@Setter
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "private")
    private boolean isPrivate;

    @Column(name = "text")
    private String text;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "id_user")
    private Long userId;

    @Column(name = "id_group")
    private Long groupId;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "hidden_from", joinColumns = @JoinColumn(name = "id_post"), inverseJoinColumns = @JoinColumn(name = "id_user"))
    private Set<User> hiddenFrom = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "post_item", joinColumns = @JoinColumn(name = "id_post"), inverseJoinColumns = @JoinColumn(name = "id_item"))
    private Set<Item> items = new HashSet<>();

    public Post(Long id, boolean isPrivate, String text, LocalDateTime createdDate, boolean deleted, Long userId,
            Long groupId, Set<User> hiddenFrom, Set<Item> items) {
        this.id = id;
        this.isPrivate = isPrivate;
        this.text = text;
        this.createdDate = createdDate;
        this.deleted = deleted;
        this.userId = userId;
        this.groupId = groupId;
        this.hiddenFrom = hiddenFrom;
        this.items = items;
    }

    public Post() {
    }

    public Post(boolean isPrivate, String text, LocalDateTime createdDate, boolean deleted, Long userId, Long groupId,
            Set<User> hiddenFrom, Set<Item> items) {
        this.isPrivate = isPrivate;
        this.text = text;
        this.createdDate = createdDate;
        this.deleted = deleted;
        this.userId = userId;
        this.groupId = groupId;
        this.hiddenFrom = hiddenFrom;
        this.items = items;
    }

}
