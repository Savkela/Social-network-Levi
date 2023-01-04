package com.levi9.socialnetwork.Service;

import com.levi9.socialnetwork.Exception.ResourceExistsException;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.MuteDuration;
import com.levi9.socialnetwork.Model.MuteGroup;

import java.util.List;

public interface MuteGroupService {
    List<MuteGroup> getAllMuteGroups();

    MuteGroup getMuteGroupByIds(Long userId, Long groupId) throws ResourceNotFoundException;

    MuteGroup createMuteGroup(MuteGroup muteGroup) throws ResourceExistsException;

    MuteGroup muteGroup(Long userId, Long groupId, MuteDuration muteDuration)
            throws ResourceExistsException, ResourceNotFoundException;

    MuteGroup unmuteGroup(Long userId, Long groupId) throws ResourceNotFoundException;

    void deleteMuteGroup(Long userId, Long groupId) throws ResourceNotFoundException;

    MuteDuration getMuteDurationFromString(String muteDurationName);
}
