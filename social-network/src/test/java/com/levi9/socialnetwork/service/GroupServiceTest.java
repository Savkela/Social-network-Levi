package com.levi9.socialnetwork.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.not;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import com.levi9.socialnetwork.Exception.ResourceExistsException;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Group;
import com.levi9.socialnetwork.Model.MuteGroup;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Repository.GroupRepository;
import com.levi9.socialnetwork.Repository.UserRepository;
import com.levi9.socialnetwork.Service.MuteGroupService;
import com.levi9.socialnetwork.Service.impl.GroupServiceImpl;
import com.levi9.socialnetwork.dto.GroupDTO;
import com.levi9.socialnetwork.dto.RequestDTO;

@RunWith(SpringRunner.class)
public class GroupServiceTest {
    private static final Long GROUP_ID = 1L;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MuteGroupService muteGroupService;

    @InjectMocks
    private GroupServiceImpl groupService;

    Principal principal;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        principal = new Principal() {

            @Override
            public String getName() {
                return "user1";
            }
        };
    }

    @Test
    void testGetAllGroups() {
        List<Group> groups = new ArrayList<Group>();
        Group group1 = new Group(false, GROUP_ID, "Group 1");
        Group group2 = new Group(false, GROUP_ID, "Group 2");
        groups.add(group1);
        groups.add(group2);
        given(groupRepository.findAll()).willReturn(groups);

        assertThat(groupService.getAllGroups()).hasSize(2);
        verify(groupRepository, times(1)).findAll();
    }

    @Test
    void testGetGroup() throws ResourceNotFoundException {

        Group expectedGroup = new Group(false, 1L, "Group 1");
        when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(expectedGroup));

        Group realGroup = groupService.getGroupById(1L);

        assertThat(realGroup).usingRecursiveComparison().isEqualTo(expectedGroup);
        verify(groupRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(groupRepository);
    }

    @Test
    void testGetNotExistingGroupShouldReturnException() throws ResourceNotFoundException {
        given(groupRepository.findById(GROUP_ID)).willAnswer(invocation -> {
            throw new ResourceNotFoundException("Group not found");
        });

        assertThrows(ResourceNotFoundException.class, () -> groupService.getGroupById(2L));
        verify(groupRepository, times(1)).findById(2L);
        verifyNoMoreInteractions(groupRepository);
    }

    @Test
    void testCreateGroup() {
        Group group = new Group(false, 1L, "New group");
        group.setId(GROUP_ID);
        GroupDTO groupDTO = new GroupDTO(false, "New group");
        User user = new User(1L, "John", "Smith", "user1", "123");

        given(userRepository.findByUsername(principal.getName())).willReturn(user);
        given(groupRepository.save(any(Group.class))).willReturn(group);

        Group createdGroup = groupService.createGroup(groupDTO, principal);
        assertThat(createdGroup).isNotNull();

        assertThat(createdGroup.getIdAdmin()).isEqualTo(group.getIdAdmin());
        assertThat(createdGroup.getName()).isEqualTo(group.getName());
    }

    @Test
    void testUpdateGroup() throws ResourceNotFoundException {
        Group group = new Group(false, 1L, "New group");
        group.setId(GROUP_ID);
        GroupDTO groupDTO = new GroupDTO(false, "New group");

        given(groupRepository.findById(group.getId())).willReturn(Optional.of(group));
        given(groupRepository.save(group)).willReturn(group);

        groupDTO.setName("Updated group");
        groupDTO.setPrivate(true);
        Group updatedGroup = groupService.updateGroup(GROUP_ID, groupDTO);

        assertThat(updatedGroup.getName()).isEqualTo("Updated group");
    }

    @Test
    void testUpdateGroupNonExistingId() throws ResourceNotFoundException {
        Group group = new Group(false, 1L, "New group");
        group.setId(GROUP_ID);
        GroupDTO groupDTO = new GroupDTO(false, "New group");

        given(groupRepository.findById(group.getId())).willAnswer(invocation -> {
            throw new ResourceNotFoundException("Post with id " + group.getId() + " was not found");
        });
        given(groupRepository.save(group)).willReturn(group);

        groupDTO.setName("Updated group");
        groupDTO.setPrivate(true);

        assertThrows(ResourceNotFoundException.class, () -> {
            groupService.updateGroup(2L, groupDTO);
        });
    }

    @Test
    void testDeleteGroup() throws ResourceNotFoundException {
        Group group = new Group(false, 1L, "New group");
        group.setId(GROUP_ID);
        when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(group));

        groupService.deleteGroup(group.getId());
        verify(groupRepository, times(1)).delete(group);
    }

    @Test
    void testDeleteNonExistingGroup() throws ResourceNotFoundException {
        Group group = new Group(false, 1L, "New group");
        group.setId(GROUP_ID);
        when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(group));

        groupService.deleteGroup(group.getId());

        assertThrows(ResourceNotFoundException.class, () -> {
            groupService.deleteGroup(2L);
        });
    }

    @Test
    void testAddUserToGroup() throws ResourceNotFoundException, ResourceExistsException {
        Group group = new Group(false, 1L, "Group 1");
        group.setId(GROUP_ID);
        User user = new User(1L, "John", "Smith", "user1", "123");
        MuteGroup muteGroup = new MuteGroup(user.getId(), group.getId(), false, LocalDateTime.now());
        RequestDTO requestDTO = new RequestDTO(user.getId(), group.getId());

        given(groupRepository.findById(GROUP_ID)).willReturn(Optional.of(group));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(groupRepository.save(group)).willReturn(group);
        given(muteGroupService.createMuteGroup(muteGroup)).willReturn(muteGroup);
        groupService.addUserToGroup(requestDTO);

        assertThat(group.getMembers(), hasItem(hasProperty("name", is("John"))));
    }
    
    @Test
    void testAddBadUserToGroupShouldReturnException() throws ResourceNotFoundException, ResourceExistsException {
        Group group = new Group(false, 1L, "Group 1");
        group.setId(GROUP_ID);
        User user = new User(1L, "John", "Smith", "user1", "123");
        MuteGroup muteGroup = new MuteGroup(user.getId(), group.getId(), false, LocalDateTime.now());
        RequestDTO requestDTO = new RequestDTO(user.getId(), group.getId());

        given(groupRepository.findById(GROUP_ID)).willReturn(Optional.of(group));
        given(userRepository.findById(1L)).willReturn(Optional.empty());
        given(groupRepository.save(group)).willReturn(group);
        given(muteGroupService.createMuteGroup(muteGroup)).willReturn(muteGroup);
        
        assertThrows(ResourceNotFoundException.class, () -> {
            groupService.addUserToGroup(requestDTO);
        });
    }
    
    @Test
    void testAddUserToBadGroupShouldReturnException() throws ResourceNotFoundException, ResourceExistsException {
        Group group = new Group(false, 1L, "Group 1");
        group.setId(GROUP_ID);
        User user = new User(1L, "John", "Smith", "user1", "123");
        MuteGroup muteGroup = new MuteGroup(user.getId(), group.getId(), false, LocalDateTime.now());
        RequestDTO requestDTO = new RequestDTO(user.getId(), group.getId());

        given(groupRepository.findById(GROUP_ID)).willReturn(Optional.empty());
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(groupRepository.save(group)).willReturn(group);
        given(muteGroupService.createMuteGroup(muteGroup)).willReturn(muteGroup);
        
        assertThrows(ResourceNotFoundException.class, () -> {
            groupService.addUserToGroup(requestDTO);
        });
    }

    @Test
    void testAddExistingMemberToGroupShouldReturnException() throws ResourceNotFoundException, ResourceExistsException {
        Group group = new Group(false, 1L, "Group 1");
        group.setId(GROUP_ID);
        User user = new User(1L, "John", "Smith", "user1", "123");
        MuteGroup muteGroup = new MuteGroup(user.getId(), group.getId(), false, LocalDateTime.now());
        RequestDTO requestDTO = new RequestDTO(user.getId(), group.getId());

        given(groupRepository.findById(GROUP_ID)).willReturn(Optional.of(group));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(groupRepository.save(group)).willReturn(group);
        given(muteGroupService.createMuteGroup(muteGroup)).willReturn(muteGroup);
        groupService.addUserToGroup(requestDTO);

        assertThrows(ResourceExistsException.class, () -> {
            groupService.addUserToGroup(requestDTO);
        });
    }

    @Test
    void testDeleteMemberEvents() {
        groupService.deleteMemberEvents(1L, GROUP_ID);
        verify(groupRepository, times(1)).deleteMemberEvents(1L, GROUP_ID);
        verifyNoMoreInteractions(groupRepository);
    }

    @Test
    void testAcceptMember() throws ResourceNotFoundException, ResourceExistsException {
        Group group = new Group(false, 1L, "Group 1");
        group.setId(GROUP_ID);
        User user = new User(1L, "John", "Smith", "user1", "123");
        MuteGroup muteGroup = new MuteGroup(user.getId(), group.getId(), false, LocalDateTime.now());
        group.getUserRequests().add(user);
        
        given(groupRepository.findById(GROUP_ID)).willReturn(Optional.of(group));
        given(groupRepository.save(group)).willReturn(group);
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(muteGroupService.createMuteGroup(muteGroup)).willReturn(muteGroup);
        
        groupService.acceptMember(1L,  GROUP_ID);
        
        assertThat(group.getMembers(), hasItem(hasProperty("name", is("John"))));
        
    }
    
    @Test
    void testAcceptMemberWithoutRequestShouldReturnException() throws ResourceNotFoundException, ResourceExistsException {
        Group group = new Group(false, 1L, "Group 1");
        group.setId(GROUP_ID);
        User user = new User(1L, "John", "Smith", "user1", "123");
        MuteGroup muteGroup = new MuteGroup(user.getId(), group.getId(), false, LocalDateTime.now());
        
        given(groupRepository.findById(GROUP_ID)).willReturn(Optional.of(group));
        given(groupRepository.save(group)).willReturn(group);
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(muteGroupService.createMuteGroup(muteGroup)).willReturn(muteGroup);
        
        assertThrows(ResourceNotFoundException.class, () -> {
            groupService.acceptMember(1L, GROUP_ID);
        });
        
    }
 
    @Test
    void testRemoveMember() throws ResourceNotFoundException {
        Group group = new Group(false, 1L, "Group 1");
        group.setId(GROUP_ID);
        User user = new User(1L, "John", "Smith", "user1", "123");
        group.getMembers().add(user);
        
        given(groupRepository.findById(GROUP_ID)).willReturn(Optional.of(group));
        given(groupRepository.save(group)).willReturn(group);
        groupService.removeMember(user.getId(), group.getId());
        
        assertThat(group.getMembers(), not(hasItem(hasProperty("name", is("John")))));
    }
    
    @Test
    void testRemoveNonExistingMember() throws ResourceNotFoundException {
        Group group = new Group(false, 1L, "Group 1");
        group.setId(GROUP_ID);
        User user = new User(1L, "John", "Smith", "user1", "123");
        
        given(groupRepository.findById(GROUP_ID)).willReturn(Optional.of(group));
        given(groupRepository.save(group)).willReturn(group);
        
        assertThrows(ResourceNotFoundException.class, () -> {
            groupService.removeMember(user.getId(), group.getId());
        });
    }
}
