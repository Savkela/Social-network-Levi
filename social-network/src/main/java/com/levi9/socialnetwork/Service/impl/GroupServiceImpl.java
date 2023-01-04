package com.levi9.socialnetwork.Service.impl;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.levi9.socialnetwork.Exception.ResourceExistsException;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Group;
import com.levi9.socialnetwork.Model.MuteGroup;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Repository.GroupRepository;
import com.levi9.socialnetwork.Repository.UserRepository;
import com.levi9.socialnetwork.Service.GroupService;
import com.levi9.socialnetwork.Service.MuteGroupService;
import com.levi9.socialnetwork.dto.GroupDTO;
import com.levi9.socialnetwork.dto.GroupResponseDTO;
import com.levi9.socialnetwork.dto.RequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.transaction.Transactional;

@Service
public class GroupServiceImpl implements GroupService {
    private static final String RESOURCE_NOT_FOUND_MESSAGE = "Group is not found for this id ::";
    private static final String USER_NOT_FOUND_MESSAGE = "User is not found for this id ::";

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MuteGroupService muteGroupService;

    public List<GroupResponseDTO> getAllGroups() {
        List<Group> groups = groupRepository.findAll();
        List<GroupResponseDTO> groupResponseDTOs = new ArrayList<>();
        for (Group group : groups) {
            GroupResponseDTO groupDTO = new GroupResponseDTO(group);
            groupResponseDTOs.add(groupDTO);
        }
        return groupResponseDTOs;
    }

    public Group getGroupById(Long id) throws ResourceNotFoundException {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE + id));
    }

    public Group createGroup(GroupDTO groupDTO, Principal principal) {
        String adminUsername = principal.getName();
        User loggedUser = userRepository.findByUsername(adminUsername);
        Long adminId = loggedUser.getId();
        
        Group group = new Group(groupDTO.isPrivate(), adminId, groupDTO.getName());
        Group addedGroup = groupRepository.save(group);
        return addedGroup;
    }

    public Group updateGroup(Long groupId, @RequestBody GroupDTO groupDTO) throws ResourceNotFoundException {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE + groupId));
        group.setPrivate(groupDTO.isPrivate());
        group.setName(groupDTO.getName());

        return groupRepository.save(group);
    }

    public Group deleteGroup(Long groupId) throws ResourceNotFoundException {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE + groupId));
        groupRepository.delete(group);
        return group;
    }

    public User addUserToGroup(RequestDTO requestDTO) throws ResourceNotFoundException, ResourceExistsException {
        Group group = groupRepository.findById(requestDTO.getIdGroup())
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE + requestDTO.getIdGroup()));
        User user = userRepository.findById(requestDTO.getIdUser())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + requestDTO.getIdGroup()));

        if (group.containsUser(user.getId())) {
            throw new ResourceExistsException("User is already member of group.");
        }

        group.getMembers().add(user);
        groupRepository.save(group);

        MuteGroup muteGroup = new MuteGroup(user.getId(), group.getId(), false, LocalDateTime.now());
        muteGroupService.createMuteGroup(muteGroup);

        return user;

    }

    @Override
    @Transactional
    public void deleteMemberEvents(Long userId, Long groupId) {
        groupRepository.deleteMemberEvents(userId, groupId);
    }

    @Override
    public boolean acceptMember(Long userId, Long groupId) throws ResourceNotFoundException, ResourceExistsException {

        Group group = getGroupById(groupId);
        boolean removed = group.getUserRequests().removeIf(user -> user.getId().equals(userId));
        if (!removed) {
            throw new ResourceNotFoundException("User with id " + userId + " did not request joining this group!");
        }
        User user = userRepository.findById(userId).map(u -> u).orElseThrow();
        group.getMembers().add(user);
        groupRepository.save(group);

        MuteGroup muteGroup = new MuteGroup(userId, groupId, false, LocalDateTime.now());
        muteGroupService.createMuteGroup(muteGroup);

        return true;
    }

    @Override
    public void removeMember(Long userId, Long groupId) throws ResourceNotFoundException {
        groupRepository.removeMembersFromEvents(groupId, userId);
        Group group = getGroupById(groupId);
        boolean removed = group.getMembers().removeIf(user -> user.getId().equals(userId));
        if (!removed) {
            throw new ResourceNotFoundException(
                    "User with id " + userId + " is not a member of group with id " + groupId);
        }
        groupRepository.save(group);

    }

}
