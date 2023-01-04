package com.levi9.socialnetwork.Model;

import com.levi9.socialnetwork.dto.CommentDTO;
import com.levi9.socialnetwork.dto.ReplyDTO;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment", schema = "public")
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "text")
    private String text;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "id_user")
    private Long idUser;

    @Column(name = "id_replied_to")
    private Long idRepliedTo;

    @Column(name = "id_post")
    private Long idPost;

    public Comment(Long id, String text, LocalDateTime createdDate, boolean deleted, Long idUser, Long idRepliedTo,
            Long idPost) {
        super();
        this.id = id;
        this.text = text;
        this.createdDate = createdDate;
        this.deleted = deleted;
        this.idUser = idUser;
        this.idRepliedTo = idRepliedTo;
        this.idPost = idPost;
    }
    
    public Comment(CommentDTO commentDTO) {
        super();
        this.text = commentDTO.getText();
        this.createdDate = commentDTO.getCreatedDate();
        this.deleted = false;
        this.idUser = commentDTO.getIdUser();
        this.idPost = commentDTO.getIdPost();
    }

    public Comment(ReplyDTO replyDTO) {
        super();
        this.text = replyDTO.getText();
        this.createdDate = replyDTO.getCreatedDate();
        this.deleted = false;
        this.idUser = replyDTO.getIdUser();
        this.idRepliedTo = replyDTO.getIdRepliedTo();
        this.idPost = replyDTO.getIdPost();
    }

}
