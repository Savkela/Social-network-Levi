package com.levi9.socialnetwork.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class RequestDTO {

	private Long idUser;
	
	private Long idGroup;

	public RequestDTO(Long idUser, Long idGroup) {
		super();
		this.idUser = idUser;
		this.idGroup = idGroup;
	}
	
}
