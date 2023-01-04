package com.levi9.socialnetwork.dto;

import java.time.LocalDateTime;

import com.levi9.socialnetwork.Model.MuteGroup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MuteGroupDTO {
    private Long userId;
    private Long groupId;
    private Boolean isPermanent;
    private LocalDateTime endOfMute;

    public MuteGroupDTO(Long userId, Long groupId, Boolean isPermanent, LocalDateTime endOfMute) {
        this.userId = userId;
        this.groupId = groupId;
        this.isPermanent = isPermanent;
        this.endOfMute = endOfMute;
    }

    public MuteGroupDTO(MuteGroup muteGroup) {
        this.userId = muteGroup.getUserId();
        this.groupId = muteGroup.getGroupId();
        this.isPermanent = muteGroup.getIsPermanent();
        this.endOfMute = muteGroup.getEndOfMute();
    }

}
