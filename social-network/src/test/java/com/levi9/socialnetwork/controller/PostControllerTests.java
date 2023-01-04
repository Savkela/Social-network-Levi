package com.levi9.socialnetwork.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi9.socialnetwork.Controller.PostController;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Group;
import com.levi9.socialnetwork.Model.Post;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Service.impl.GroupServiceImpl;
import com.levi9.socialnetwork.Service.impl.PostServiceImpl;
import com.levi9.socialnetwork.Service.impl.UserServiceImpl;
import com.levi9.socialnetwork.dto.CreatePostDTO;
import com.levi9.socialnetwork.dto.PostDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTests {
    @InjectMocks
    private PostController postController;

    @MockBean
    private PostServiceImpl postService;

    @MockBean
    private GroupServiceImpl groupService;

    @MockBean
    private UserServiceImpl userService;

    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() throws Exception {
        try (var ignored = MockitoAnnotations.openMocks(this)) {
            this.mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        }
    }

    @Test
    void testGetOne() throws Exception {
        givenPostIdShouldReturnPost();
        givenPostIdShouldReturnNotFound();
    }

    @Test
    void testGetAllPostsFromFriends() throws Exception {
        givenUserIdShouldReturnPostsFromFriends();
        givenUserIdShouldReturnNotFoundFriends();
    }

    @Test
    void testGetAllPostsFromFriendsFromPublicGroups() throws Exception {
        givenUserIdShouldReturnPostsFromFriendsFromPublicGroups();
        givenUserIdShouldReturnNotFoundFriendsPublicGroups();
    }

    @Test
    void testGetAllPostsFromFriendsFromPrivateGroups() throws Exception {
        givenUserIdShouldReturnPostsFromFriendsFromPrivateGroups();
        givenUserIdShouldReturnNotFoundFriendsPrivateGroups();
    }

    @Test
    void testCreatePost() throws Exception {
        givenPostDTOAndPrincipalCreatePost();
        givenPostDTOAndPrincipalReturnNotFound();
        givenPostDTOAndPrincipalReturnUnauthorized();
        givenPostDTOAndPrincipalReturnBadRequest();
    }

    @Test
    void testUpdatePost() throws Exception {
        givenIdPostDTOAndPrincipalUpdateAndReturnPost();
        givenIdPostDTOAndPrincipalReturnNotFound();
        givenIdPostDTOAndPrincipalReturnForbidden();
    }

    @Test
    void testDeletePost() throws Exception {
        givenIdAndPrincipalReturnNoContent();
        givenIdAndPrincipalReturnForbidden();
        givenIdAndPrincipalReturnNotFound();
    }

    void givenPostIdShouldReturnPost() throws Exception {
        PostDTO postDTO = PostDTO.builder().id(1L).build();
        given(postService.getPostById(1L))
                .willReturn(postDTO);

        mockMvc.perform(get("/api/posts/{id}", 1L))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1L))
                .andDo(print());
    }

    void givenPostIdShouldReturnNotFound() throws Exception {
        given(postService.getPostById(2L))
                .willThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/posts/{id}", 2L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    void givenUserIdShouldReturnPostsFromFriends() throws Exception {
        List<Post> posts = List.of(
                Post.builder().id(1L).build(),
                Post.builder().id(2L).build()
        );
        given(postService.getAllPostsFromFriends(1L))
                .willReturn(posts);

        mockMvc.perform(get("/api/posts/friendPosts/{userId}", 1L))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()", is(2)),
                        jsonPath("$[0].id").value(1L),
                        jsonPath("$[1].id").value(2L)
                )
                .andDo(print());
    }

    void givenUserIdShouldReturnNotFoundFriends() throws Exception {
        given(postService.getAllPostsFromFriends(2L))
                .willThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/posts/friendPosts/{userId}", 2L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    void givenUserIdShouldReturnPostsFromFriendsFromPublicGroups() throws Exception {
        List<Post> posts = List.of(
                Post.builder().id(1L).build(),
                Post.builder().id(2L).build()
        );
        given(postService.getAllPostsOfMyFriendsFromPublicGroups(1L))
                .willReturn(posts);

        mockMvc.perform(get("/api/posts/friendPostsPublicGroups/{userId}", 1L))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()", is(2)),
                        jsonPath("$[0].id").value(1L),
                        jsonPath("$[1].id").value(2L)
                )
                .andDo(print());
    }

    void givenUserIdShouldReturnNotFoundFriendsPublicGroups() throws Exception {
        given(postService.getAllPostsOfMyFriendsFromPublicGroups(2L))
                .willThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/posts/friendPostsPublicGroups/{userId}", 2L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    void givenUserIdShouldReturnPostsFromFriendsFromPrivateGroups() throws Exception {
        List<Post> posts = List.of(
                Post.builder().id(1L).build(),
                Post.builder().id(2L).build()
        );
        given(postService.getAllPostsOfMyFriendsFromPrivateGroups(1L))
                .willReturn(posts);

        mockMvc.perform(get("/api/posts/friendPostsPrivateGroups/{userId}", 1L))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()", is(2)),
                        jsonPath("$[0].id").value(1L),
                        jsonPath("$[1].id").value(2L)
                )
                .andDo(print());
    }

    void givenUserIdShouldReturnNotFoundFriendsPrivateGroups() throws Exception {
        given(postService.getAllPostsOfMyFriendsFromPrivateGroups(2L))
                .willThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/posts/friendPostsPrivateGroups/{userId}", 2L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    void givenPostDTOAndPrincipalCreatePost() throws Exception {
        CreatePostDTO createPostDTO = CreatePostDTO.builder()
                .text("Lorem ipsum")
                .createdDate(LocalDateTime.now())
                .groupId(null)
                .build();
        Principal principal = () -> "givenPostDTOAndPrincipalCreatePost";
        User user = User.builder()
                .id(1L)
                .username(principal.getName())
                .build();

        given(userService.findUserByUsername(principal.getName()))
                .willReturn(user);
        given(postService.createPost(createPostDTO, user.getId()))
                .willReturn(1L);

        mockMvc.perform(post("/api/posts")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(createPostDTO))
                        .characterEncoding("utf-8"))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$").value(1L)
                )
                .andDo(print());
    }

    void givenPostDTOAndPrincipalReturnNotFound() throws Exception {
        CreatePostDTO createPostDTO = CreatePostDTO.builder()
                .text("Lorem ipsum")
                .createdDate(LocalDateTime.now())
                .groupId(null)
                .build();

        Principal principal1 = () -> "givenPostDTOAndPrincipalReturnNotFound1";
        given(userService.findUserByUsername(principal1.getName()))
                .willThrow(ResourceNotFoundException.class);

        mockMvc.perform(post("/api/posts")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .principal(principal1)
                        .content(objectMapper.writeValueAsString(createPostDTO))
                        .characterEncoding("utf-8"))
                .andExpectAll(
                        status().isNotFound()
                )
                .andDo(print());

        Principal principal2 = () -> "givenPostDTOAndPrincipalReturnNotFound2";
        createPostDTO.setGroupId(1L);
        User user = User.builder()
                .id(1L)
                .username(principal2.getName())
                .build();

        given(userService.findUserByUsername(principal2.getName()))
                .willReturn(user);
        given(groupService.getGroupById(createPostDTO.getGroupId()))
                .willThrow(ResourceNotFoundException.class);

        mockMvc.perform(post("/api/posts")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .principal(principal2)
                        .content(objectMapper.writeValueAsString(createPostDTO))
                        .characterEncoding("utf-8"))
                .andExpectAll(
                        status().isNotFound()
                )
                .andDo(print());
    }

    void givenPostDTOAndPrincipalReturnUnauthorized() throws Exception {
        CreatePostDTO createPostDTO = CreatePostDTO.builder()
                .text("Lorem ipsum")
                .createdDate(LocalDateTime.now())
                .groupId(null)
                .build();
        Principal principal = () -> "givenPostDTOAndPrincipalReturnUnauthorized";
        given(userService.findUserByUsername(principal.getName()))
                .willReturn(null);

        mockMvc.perform(post("/api/posts")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(createPostDTO))
                        .characterEncoding("utf-8"))
                .andExpectAll(
                        status().isUnauthorized()
                )
                .andDo(print());
    }

    void givenPostDTOAndPrincipalReturnBadRequest() throws Exception {
        CreatePostDTO createPostDTO = CreatePostDTO.builder()
                .text("Lorem ipsum")
                .createdDate(LocalDateTime.now())
                .groupId(2L)
                .build();
        Principal principal = () -> "givenPostDTOAndPrincipalReturnBadRequest";
        User user = User.builder()
                .id(1L)
                .username(principal.getName())
                .build();
        Set<User> users = new HashSet<>();
        Group group = Group.builder().id(2L).members(users).build();

        given(userService.findUserByUsername(principal.getName()))
                .willReturn(user);
        given(groupService.getGroupById(2L))
                .willReturn(group);

        mockMvc.perform(post("/api/posts")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(createPostDTO))
                        .characterEncoding("utf-8"))
                .andExpectAll(
                        status().isBadRequest()
                )
                .andDo(print());

        users.add(user);
        group.setMembers(users);
        given(postService.createPost(createPostDTO, 1L))
                .willAnswer(invocation -> {
                    throw new ResourceNotFoundException("");
                });

        mockMvc.perform(post("/api/posts")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(createPostDTO))
                        .characterEncoding("utf-8"))
                .andExpectAll(
                        status().isBadRequest()
                )
                .andDo(print());
    }

    void givenIdPostDTOAndPrincipalUpdateAndReturnPost() throws Exception {
        Principal principal = () -> "givenIdPostDTOAndPrincipalUpdateAndReturnPost";
        User user = User.builder()
                .id(1L)
                .username(principal.getName())
                .build();
        PostDTO postDTO = PostDTO.builder()
                .id(1L)
                .userId(user.getId())
                .text("Lorem")
                .build();
        PostDTO updatedPostDTO = PostDTO.builder()
                .id(1L)
                .userId(user.getId())
                .text("Lorem ipsum")
                .build();

        given(userService.findUserByUsername(principal.getName()))
                .willReturn(user);
        given(postService.getPostById(1L))
                .willReturn(postDTO);
        given(postService.updatePost(1L, postDTO))
                .willReturn(updatedPostDTO);

        mockMvc.perform(put("/api/posts/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(postDTO))
                        .characterEncoding("utf-8"))
                .andExpectAll(
                        status().isOk()
                )
                .andDo(print());
    }

    void givenIdPostDTOAndPrincipalReturnNotFound() throws Exception {
        Principal principal = () -> "givenIdPostDTOAndPrincipalReturnNotFound";
        User user = User.builder()
                .id(1L)
                .username(principal.getName())
                .build();
        PostDTO postDTO = PostDTO.builder()
                .id(1L)
                .userId(2L)
                .text("Lorem")
                .build();

        given(userService.findUserByUsername(principal.getName()))
                .willReturn(user);
        given(postService.getPostById(1L))
                .willReturn(postDTO);

        mockMvc.perform(put("/api/posts/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(postDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    void givenIdPostDTOAndPrincipalReturnForbidden() throws Exception {
        Principal principal = () -> "givenIdPostDTOAndPrincipalReturnForbidden";
        User user = User.builder()
                .id(1L)
                .username(principal.getName())
                .build();
        PostDTO postDTO = PostDTO.builder()
                .id(1L)
                .userId(user.getId())
                .text("Lorem")
                .build();
        PostDTO updatedPostDTO = PostDTO.builder()
                .id(1L)
                .userId(user.getId())
                .text("Lorem ipsum")
                .build();

        given(userService.findUserByUsername(principal.getName()))
                .willReturn(user);
        given(postService.getPostById(1L))
                .willReturn(postDTO);
        given(postService.updatePost(1L, postDTO))
                .willAnswer(invocation -> {
                    throw new ResourceNotFoundException("");
                });

        mockMvc.perform(put("/api/posts/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(postDTO))
                        .characterEncoding("utf-8"))
                .andExpectAll(
                        status().isNotFound()
                )
                .andDo(print());
    }

    void givenIdAndPrincipalReturnNoContent() throws Exception {
        Principal principal = () -> "givenIdAndPrincipalReturnNoContent";
        User user = User.builder()
                .id(1L)
                .username(principal.getName())
                .build();
        PostDTO postDTO = PostDTO.builder()
                .id(1L)
                .userId(user.getId())
                .text("Lorem")
                .build();

        given(postService.getPostById(1L))
                .willReturn(postDTO);
        given(userService.findUserByUsername(principal.getName()))
                .willReturn(user);
        doNothing().when(postService).deletePost(1L);

        mockMvc.perform(delete("/api/posts/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(postDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    void givenIdAndPrincipalReturnForbidden() throws Exception {
        Principal principal = () -> "givenIdAndPrincipalReturnForbidden";
        User user = User.builder()
                .id(1L)
                .username(principal.getName())
                .build();
        PostDTO postDTO = PostDTO.builder()
                .id(1L)
                .userId(2L)
                .text("Lorem")
                .build();

        given(postService.getPostById(1L))
                .willReturn(postDTO);
        given(userService.findUserByUsername(principal.getName()))
                .willReturn(user);

        mockMvc.perform(delete("/api/posts/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(postDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    void givenIdAndPrincipalReturnNotFound() throws Exception {
        Principal principal = () -> "givenIdAndPrincipalReturnNotFound";
        User user = User.builder()
                .id(1L)
                .username(principal.getName())
                .build();
        PostDTO postDTO = PostDTO.builder()
                .id(1L)
                .userId(user.getId())
                .text("Lorem")
                .build();

        given(postService.getPostById(1L))
                .willReturn(postDTO);
        given(userService.findUserByUsername(principal.getName()))
                .willReturn(user);
        doAnswer(invocation -> {
            throw new ResourceNotFoundException("");
        }).when(postService).deletePost(1L);

        mockMvc.perform(delete("/api/posts/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(postDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

}
