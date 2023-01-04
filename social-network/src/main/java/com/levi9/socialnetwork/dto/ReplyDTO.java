package com.levi9.socialnetwork.dto;

import java.time.LocalDateTime;

import com.levi9.socialnetwork.Model.Comment;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class ReplyDTO {
	
	private String text;

	private LocalDateTime createdDate;
	
	private boolean deleted;

	private Long idUser;

	private Long idRepliedTo;
	
	private Long idPost;

	public ReplyDTO(String text, LocalDateTime createdDate, boolean deleted, Long idUser, Long idRepliedTo,
			Long idPost) {
		this.text = text;
		this.createdDate = createdDate;
		this.deleted = deleted;
		this.idUser = idUser;
		this.idRepliedTo = idRepliedTo;
		this.idPost = idPost;
	}

	public ReplyDTO(Comment reply) {
		this.text = reply.getText();
		this.createdDate = reply.getCreatedDate();
		this.deleted = reply.isDeleted();
		this.idUser = reply.getIdUser();
		this.idRepliedTo = reply.getIdRepliedTo();
		this.idPost = reply.getIdPost();
	}

}
