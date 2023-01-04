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
@Builder
@NoArgsConstructor
@EqualsAndHashCode
public class CommentDTO {

	private String text;

	private LocalDateTime createdDate;
	
	private boolean deleted;

	private Long idUser;
	
	private Long idPost;
	
	public CommentDTO(String text, LocalDateTime createdDate, boolean deleted, Long idUser, Long idPost) {
		super();
		this.text = text;
		this.createdDate = createdDate;
		this.deleted = deleted;
		this.idUser = idUser;
		this.idPost = idPost;
	}
	
	public CommentDTO(Comment comment) {
		this.text = comment.getText();
		this.createdDate = comment.getCreatedDate();
		this.deleted = comment.isDeleted();
		this.idUser = comment.getIdUser();
		this.idPost = comment.getIdPost();
	}


	
	
}
