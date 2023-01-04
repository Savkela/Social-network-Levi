package com.levi9.socialnetwork.Service;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import com.levi9.socialnetwork.Exception.ResourceExistsException;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Group;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.dto.GroupDTO;
import com.levi9.socialnetwork.dto.GroupResponseDTO;
import com.levi9.socialnetwork.dto.RequestDTO;

public interface GroupService {

    public List<GroupResponseDTO> getAllGroups();
	
    public Group getGroupById(Long id) throws ResourceNotFoundException;

    public Group createGroup(GroupDTO groupDTO, Principal principal);
	
    public Group updateGroup(Long groupId, @RequestBody GroupDTO groupDTO) throws ResourceNotFoundException;
	
    public Group deleteGroup(Long groupId) throws ResourceNotFoundException;

    public boolean acceptMember(Long userId, Long groupId) throws ResourceNotFoundException, ResourceExistsException;

	void deleteMemberEvents(Long userId, Long groupId);

    public void removeMember(Long userId, Long groupId) throws ResourceNotFoundException;

    public User addUserToGroup(RequestDTO requestDTO) throws ResourceNotFoundException, ResourceExistsException;

}
