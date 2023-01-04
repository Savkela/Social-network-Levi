package com.levi9.socialnetwork.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi9.socialnetwork.Controller.GroupController;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Address;
import com.levi9.socialnetwork.Model.Event;
import com.levi9.socialnetwork.Model.Group;
import com.levi9.socialnetwork.Model.MuteDuration;
import com.levi9.socialnetwork.Model.MuteGroup;
import com.levi9.socialnetwork.Model.Post;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Service.AddressService;
import com.levi9.socialnetwork.Service.EventService;
import com.levi9.socialnetwork.Service.GroupService;
import com.levi9.socialnetwork.Service.MuteGroupService;
import com.levi9.socialnetwork.Service.PostService;
import com.levi9.socialnetwork.Service.UserService;
import com.levi9.socialnetwork.dto.AddressDTO;
import com.levi9.socialnetwork.dto.EventDTO;
import com.levi9.socialnetwork.dto.GroupDTO;
import com.levi9.socialnetwork.dto.GroupResponseDTO;

@AutoConfigureMockMvc(addFilters = false)
@RunWith(SpringRunner.class)
@WebMvcTest(GroupController.class)
class GroupControllerTest {
    private static final String URL_PREFIX = "/api/groups";
    private static final Long GROUP_ID = 1L;
    private static final LocalDateTime startDate = LocalDateTime.of(2022, Month.NOVEMBER, 29, 19, 30, 40);
    private static final LocalDateTime endDate = LocalDateTime.of(2022, Month.DECEMBER, 20, 19, 30, 40);

    @Autowired
    MockMvc mockMvc;

    @MockBean
    GroupService groupService;

    @MockBean
    UserService userService;

    @MockBean
    MuteGroupService muteGroupService;

    @MockBean
    PostService postService;

    @MockBean
    EventService eventService;

    @MockBean
    AddressService addressService;

    Principal principal;

    @BeforeEach
    public void init() {
        principal = new Principal() {

            @Override
            public String getName() {
                return "user1";
            }
        };

    }

    @Autowired
    private ObjectMapper objectMapper;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    void testGetAllGroups() throws Exception {
        List<GroupResponseDTO> allGroups = new ArrayList<>();
        allGroups.add(new GroupResponseDTO(1L, 1L, false, "Best group"));
        given(groupService.getAllGroups()).willReturn(allGroups);
        mockMvc.perform(get(URL_PREFIX)).andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(allGroups.size())));

    }

    @Test
    void testGetGroup() throws Exception {
        Group group = new Group(false, 1L, "Best group");
        given(groupService.getGroupById(GROUP_ID)).willReturn(group);
        mockMvc.perform(get(URL_PREFIX + "/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.private", is(group.isPrivate())))
                .andExpect(jsonPath("$.idAdmin", is(group.getIdAdmin().intValue())))
                .andExpect(jsonPath("$.name", is(group.getName()))).andReturn();
    }

    @Test()
    void testGetNonExistingGroup() throws Exception {
        mockMvc.perform(get(URL_PREFIX + "/2"));
        given(groupService.getGroupById(GROUP_ID)).willReturn(null);
        exception.expect(ResourceNotFoundException.class);
    }

    @Test
    void testCreateGroup() throws Exception {
        Group newGroup = new Group(false, 1L, "New group");
        GroupDTO groupDTO = new GroupDTO(false, "New group");

        String requestBody = objectMapper.writeValueAsString(groupDTO);
        given(groupService.createGroup(groupDTO, principal)).willReturn(newGroup);
        mockMvc.perform(post(URL_PREFIX).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                .content(requestBody).principal(principal)).andExpect(status().isCreated()).andReturn();

    }

    @Test
    void testCreateGroupWithoutLoggedUser() throws Exception {
        GroupDTO groupDTO = new GroupDTO(false, "New group");
        String requestBody = objectMapper.writeValueAsString(groupDTO);
        given(groupService.createGroup(groupDTO, null)).willReturn(null);
        mockMvc.perform(post(URL_PREFIX).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                .content(requestBody)).andExpect(status().isForbidden());
    }

    @Test
    void testUpdateGroup() throws Exception {
        GroupDTO groupDTO = new GroupDTO(false, "Updated group");
        String requestBody = objectMapper.writeValueAsString(groupDTO);
        mockMvc.perform(put(URL_PREFIX + "/" + GROUP_ID).contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8").content(requestBody)).andExpect(status().isOk());
    }

    @Test
    void testDeleteGroup() throws Exception {
        Group newGroup = new Group(false, 1L, "New group");
        given(groupService.deleteGroup(GROUP_ID)).willReturn(newGroup);
        mockMvc.perform(delete(URL_PREFIX + "/1")).andExpect(status().isOk());
    }

    @Test
    void testGetAllPostsFromGroup() throws Exception {
        List<Post> posts = new ArrayList<Post>();
        Post post1 = new Post(1L, false, "Good morning", LocalDateTime.now(), false, 1L, 1L, null, null);
        Post post2 = new Post(2L, false, "Good evening", LocalDateTime.now(), false, 1L, 1L, null, null);
        posts.add(post1);
        posts.add(post2);
        given(postService.getAllPostsFromGroup(GROUP_ID, "user1")).willReturn(posts);
        mockMvc.perform(get(URL_PREFIX + "/" + GROUP_ID + "/posts").principal(principal)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(posts.size()))).andExpect(jsonPath("$[0].id").value(post1.getId()))
                .andExpect(jsonPath("$[0].private").value(post1.isPrivate()))
                .andExpect(jsonPath("$[0].text").value(post1.getText()))
                .andExpect(jsonPath("$[0].deleted").value(post1.isDeleted()))
                .andExpect(jsonPath("$[0].userId").value(post1.getUserId()))
                .andExpect(jsonPath("$[0].groupId").value(post1.getGroupId()));
    }

    @Test
    void testLeaveGroup() throws Exception {
        User user = new User(1L, "John", "Smith", "email", "123");
        given(userService.findUserByUsername("user1")).willReturn(user);
        willDoNothing().given(groupService).removeMember(1L, GROUP_ID);
        mockMvc.perform(delete(URL_PREFIX + "/" + GROUP_ID + "/leave").principal(principal)).andExpect(status().isOk());
    }

    @Test
    void testLeaveGroupUnathorized() throws Exception {
        given(userService.findUserByUsername("user1")).willReturn(null);
        willDoNothing().given(groupService).removeMember(1L, GROUP_ID);
        mockMvc.perform(delete(URL_PREFIX + "/" + GROUP_ID + "/leave").principal(principal))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetEventsInGroup() throws Exception {
        List<EventDTO> eventsDTO = new ArrayList<EventDTO>();
        AddressDTO addressDTO = new AddressDTO(1L, "Serbia", "Novi Sad", "Freedom Square", 1);
        Address address = new Address(addressDTO);
        EventDTO eventDTO = new EventDTO(1L, addressDTO, 1L, 1L, startDate, endDate);
        eventsDTO.add(eventDTO);

        List<Event> events = new ArrayList<Event>();
        Event event = new Event(eventDTO);
        event.setLocationId(addressDTO.getId());
        events.add(event);

        given(addressService.getAddressById(1L)).willReturn(address);
        given(eventService.getAllEventsInGroup(GROUP_ID)).willReturn(events);
        mockMvc.perform(get(URL_PREFIX + "/" + GROUP_ID + "/events")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(events.size()))).andExpect(jsonPath("$[0].id").value(event.getId()))
                .andExpect(jsonPath("$[0].userId").value(event.getUserId()))
                .andExpect(jsonPath("$[0].groupId").value(event.getGroupId()))
                .andExpect(jsonPath("$[0].startDate").value(event.getStartDate().toString()))
                .andExpect(jsonPath("$[0].endDate").value(event.getEndDate().toString()));
    }

    @Test
    void testCreateEventInGroup() throws Exception {
        AddressDTO addressDTO = new AddressDTO(1L, "Serbia", "Novi Sad", "Freedom Square", 1);
        Address address = new Address(addressDTO);
        EventDTO eventDTO = new EventDTO(1L, addressDTO, 1L, 1L, startDate, endDate);
        Event event = new Event(eventDTO);
        String requestBody = objectMapper.writeValueAsString(eventDTO);
        User user = new User(1L, "John", "Smith", "email", "123");
        Group newGroup = new Group(false, 1L, "New group");
        newGroup.getMembers().add(user);

        given(userService.findUserByUsername(principal.getName())).willReturn(user);
        given(groupService.getGroupById(GROUP_ID)).willReturn(newGroup);
        given(addressService.createAddress(any(Address.class))).willReturn(address);
        given(eventService.createEventInGroup(event, address, newGroup)).willReturn(event);

        mockMvc.perform(post(URL_PREFIX + "/" + GROUP_ID + "/events").principal(principal)
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateEventInGroupShouldReturnUnathorized() throws Exception {
        AddressDTO addressDTO = new AddressDTO(1L, "Serbia", "Novi Sad", "Freedom Square", 1);
        Address address = new Address(addressDTO);
        EventDTO eventDTO = new EventDTO(1L, addressDTO, 1L, 1L, startDate, endDate);
        Event event = new Event(eventDTO);
        String requestBody = objectMapper.writeValueAsString(eventDTO);
        User user = new User(1L, "John", "Smith", "email", "123");
        Group newGroup = new Group(false, 1L, "New group");
        newGroup.getMembers().add(user);

        given(userService.findUserByUsername(principal.getName())).willReturn(null);
        given(groupService.getGroupById(GROUP_ID)).willReturn(newGroup);
        given(addressService.createAddress(any(Address.class))).willReturn(address);
        given(eventService.createEventInGroup(event, address, newGroup)).willReturn(event);

        mockMvc.perform(post(URL_PREFIX + "/" + GROUP_ID + "/events").principal(principal)
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateEventInGroupShouldReturnNonFoundBecauseUserIsNotGroupMember() throws Exception {
        AddressDTO addressDTO = new AddressDTO(1L, "Serbia", "Novi Sad", "Freedom Square", 1);
        Address address = new Address(addressDTO);
        EventDTO eventDTO = new EventDTO(1L, addressDTO, 1L, 1L, startDate, endDate);
        Event event = new Event(eventDTO);
        String requestBody = objectMapper.writeValueAsString(eventDTO);
        User user = new User(1L, "John", "Smith", "email", "123");
        Group newGroup = new Group(false, 1L, "New group");

        given(userService.findUserByUsername(principal.getName())).willReturn(user);
        given(groupService.getGroupById(GROUP_ID)).willReturn(newGroup);
        given(addressService.createAddress(any(Address.class))).willReturn(address);
        given(eventService.createEventInGroup(event, address, newGroup)).willReturn(event);

        mockMvc.perform(post(URL_PREFIX + "/" + GROUP_ID + "/events").principal(principal)
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void testMuteGroup() throws Exception {
        User user = new User(1L, "John", "Smith", "email", "123");
        MuteGroup muteGroup = new MuteGroup(user.getId(),  GROUP_ID,  false, endDate);
        
        given(userService.findUserByUsername(principal.getName())).willReturn(user);
        given(muteGroupService.getMuteDurationFromString("HOURS_24")).willReturn(MuteDuration.HOURS_24);
        given(muteGroupService.muteGroup(user.getId(), GROUP_ID, MuteDuration.HOURS_24)).willReturn(muteGroup);
        
        String requestBody = "HOURS_24";
        
        mockMvc.perform(put(URL_PREFIX + "/" + GROUP_ID + "/mute").principal(principal).content(requestBody).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(muteGroup.getUserId()))
                .andExpect(jsonPath("$.groupId").value(muteGroup.getGroupId()))
                .andExpect(jsonPath("$.isPermanent").value(muteGroup.getIsPermanent()))
                .andExpect(jsonPath("$.endOfMute".toString()).value(muteGroup.getEndOfMute().toString()));
    }
    
    @Test
    void testMuteGroupShouldThrowUnathorized() throws Exception {
        User user = new User(1L, "John", "Smith", "email", "123");
        MuteGroup muteGroup = new MuteGroup(user.getId(),  GROUP_ID,  false, endDate);
        
        given(userService.findUserByUsername(principal.getName())).willReturn(null);
        given(muteGroupService.getMuteDurationFromString("HOURS_24")).willReturn(MuteDuration.HOURS_24);
        given(muteGroupService.muteGroup(user.getId(), GROUP_ID, MuteDuration.HOURS_24)).willReturn(muteGroup);
        
        String requestBody = "HOURS_24";
        
        mockMvc.perform(put(URL_PREFIX + "/" + GROUP_ID + "/mute").principal(principal).content(requestBody).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUnmuteGroup() throws Exception {
        User user = new User(1L, "John", "Smith", "email", "123");
        MuteGroup muteGroup = new MuteGroup(user.getId(), GROUP_ID, false, endDate);
        given(userService.findUserByUsername(principal.getName())).willReturn(user);
        given(muteGroupService.unmuteGroup(user.getId(), GROUP_ID)).willReturn(muteGroup);

        mockMvc.perform(put(URL_PREFIX + "/" + GROUP_ID + "/unmute").principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void testUnmuteGroupShouldThrowUnathorized() throws Exception {
        User user = new User(1L, "John", "Smith", "email", "123");
        MuteGroup muteGroup = new MuteGroup(user.getId(), GROUP_ID, false, endDate);
        given(userService.findUserByUsername(principal.getName())).willReturn(null);
        given(muteGroupService.unmuteGroup(user.getId(), GROUP_ID)).willReturn(muteGroup);

        mockMvc.perform(put(URL_PREFIX + "/" + GROUP_ID + "/unmute").principal(principal))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAcceptMember() throws Exception {

        Group group = new Group(false, 1L, "Group 1");
        group.setId(GROUP_ID);
        User user = new User(1L, "John", "Smith", "user1", "123");

        given(groupService.getGroupById(GROUP_ID)).willReturn(group);
        given(userService.findUserByUsername(principal.getName())).willReturn(user);
        given(groupService.acceptMember(user.getId(), GROUP_ID)).willReturn(true);

        mockMvc.perform(post(URL_PREFIX + "/" + GROUP_ID + "/accept-member/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void testAcceptMemberNoAuthShouldReturnForbidden() throws Exception {

        Group group = new Group(false, 2L, "Group 1");
        group.setId(GROUP_ID);
        User user = new User(1L, "John", "Smith", "user1", "123");

        given(groupService.getGroupById(GROUP_ID)).willReturn(group);
        given(userService.findUserByUsername(principal.getName())).willReturn(user);
        given(groupService.acceptMember(user.getId(), GROUP_ID)).willReturn(true);

        mockMvc.perform(post(URL_PREFIX + "/" + GROUP_ID + "/accept-member/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").principal(principal))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRemoveMember() throws Exception {

        Group group = new Group(false, 1L, "Group 1");
        group.setId(GROUP_ID);
        User user = new User(1L, "John", "Smith", "user1", "123");

        given(groupService.getGroupById(GROUP_ID)).willReturn(group);
        given(userService.findUserByUsername(principal.getName())).willReturn(user);
        groupService.deleteMemberEvents(user.getId(), GROUP_ID);
        groupService.removeMember(user.getId(), GROUP_ID);

        mockMvc.perform(delete(URL_PREFIX + "/" + GROUP_ID + "/remove-member/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").principal(principal))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testRemoveMemberShouldReturnForbidden() throws Exception {

        Group group = new Group(false, 2L, "Group 1");
        group.setId(GROUP_ID);
        User user = new User(1L, "John", "Smith", "user1", "123");

        given(groupService.getGroupById(GROUP_ID)).willReturn(group);
        given(userService.findUserByUsername(principal.getName())).willReturn(user);
        groupService.deleteMemberEvents(user.getId(), GROUP_ID);
        groupService.removeMember(user.getId(), GROUP_ID);

        mockMvc.perform(delete(URL_PREFIX + "/" + GROUP_ID + "/remove-member/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").principal(principal))
                .andExpect(status().isForbidden());
    }
}
