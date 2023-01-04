package com.levi9.socialnetwork.service;

import com.levi9.socialnetwork.Exception.BadRequestException;
import com.levi9.socialnetwork.Exception.ResourceExistsException;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Group;
import com.levi9.socialnetwork.Model.Post;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Repository.GroupRepository;
import com.levi9.socialnetwork.Repository.PostRepository;
import com.levi9.socialnetwork.Repository.UserRepository;
import com.levi9.socialnetwork.Service.impl.EmailServiceImpl;
import com.levi9.socialnetwork.Service.impl.PostServiceImpl;
import com.levi9.socialnetwork.dto.CreatePostDTO;
import com.levi9.socialnetwork.dto.PostDTO;
import com.levi9.socialnetwork.mapper.PostMapper;
import liquibase.pro.packaged.P;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;

@RunWith(SpringRunner.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private static final Long groupId = 2L;
    private static final Long userId = 1L;
    private static final Long postId = 1L;
    private static final String username = "User1";

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    void shouldFindAndReturnOnePost() throws ResourceNotFoundException {

        Post expectedPost = Post.builder().id(postId)
                .groupId(groupId)
                .userId(userId)
                .hiddenFrom(new HashSet<>())
                .items(new HashSet<>()).
                build();

        when(postRepository.findPostById(postId)).thenReturn(Optional.of(expectedPost));

        PostDTO actualPost = postService.getPostById(postId);

        assertThat(actualPost).usingRecursiveComparison().isEqualTo(PostMapper.MapEntityToDTO(expectedPost));
        verify(postRepository, times(1)).findPostById(postId);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    void shouldNotFindPostAndThrowException(){

        given(postRepository.findPostById(postId)).willAnswer(invocation -> {
            throw new ResourceNotFoundException("Post with id " + postId + " was not found");
        });

        assertThrows(ResourceNotFoundException.class, () -> {
            postRepository.findPostById(postId);
        });

    }

    @Test
    void testCreatePost() throws ResourceExistsException, ResourceNotFoundException {

        Post post = Post.builder()
                .id(postId)
                .groupId(groupId)
                .userId(userId)
                .hiddenFrom(new HashSet<>())
                .items(new HashSet<>()).
                build();


        CreatePostDTO createPostDTO = CreatePostDTO.builder()
                .groupId(groupId)
                .hiddenFrom(new HashSet<>())
                .items(new HashSet<>())
                .build();

        when(postRepository.save(any(Post.class))).thenReturn(post);

        Long actualId = postService.createPost(createPostDTO, userId);

        assertEquals(postId, actualId);
        verify(postRepository, times(1)).save(any(Post.class));
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    void testGetAllPostsFromFriendsShouldReturnListOfPosts() throws ResourceNotFoundException {

        given(postRepository.getAllPostsFromFriends(userId)).willReturn(List.of(new Post(), new Post(), new Post()));

        assertThat(postService.getAllPostsFromFriends(userId)).hasSize(3);
        verify(postRepository, times(1)).getAllPostsFromFriends(userId);
    }

    @Test
    void testGetAllPostsOfMyFriendsFromPublicGroupsShouldReturnListOfPosts() throws ResourceNotFoundException {

        given(postRepository.getAllPostsOfMyFriendsFromPublicGroups(userId)).willReturn(List.of(new Post(), new Post(), new Post()));

        assertThat(postService.getAllPostsOfMyFriendsFromPublicGroups(userId)).hasSize(3);
        verify(postRepository, times(1)).getAllPostsOfMyFriendsFromPublicGroups(userId);
    }

    @Test
    void testGetAllPostsOfMyFriendsFromPrivateGroupsShouldReturnListOfPosts() throws ResourceNotFoundException {

        given(postRepository.getAllPostsOfMyFriendsFromPrivateGroups(userId)).willReturn(List.of(new Post(), new Post()));

        assertThat(postService.getAllPostsOfMyFriendsFromPrivateGroups(userId)).hasSize(2);
        verify(postRepository, times(1)).getAllPostsOfMyFriendsFromPrivateGroups(userId);
    }

    @Test
    void testGetAllPostsFromGroupShouldReturnListOfPosts() throws ResourceNotFoundException {


        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        Group group = Group.builder()
                .id(groupId)
                .idAdmin(userId)
                .isPrivate(false)
                .members(Set.of(user))
                .build();

        given(userRepository.findByUsername(username)).willReturn(user);
        given(groupRepository.findById(groupId)).willReturn(Optional.of(group));
        given(postRepository.getAllPostsFromGroup(groupId, userId)).willReturn(List.of(new Post(), new Post()));

        assertThat(postService.getAllPostsFromGroup(groupId, username)).hasSize(2);
        verify(postRepository, times(1)).getAllPostsFromGroup(groupId, userId);
    }

    @Test
    void testGetAllPostsFromGroupShouldThrowResourceNotFoundException() throws ResourceNotFoundException{

        User user = new User();
        user.setId(userId);
        user.setUsername(username);

        given(userRepository.findByUsername(username)).willReturn(user);
        given(groupRepository.findById(groupId)).willAnswer(invocation -> {
            throw new ResourceNotFoundException("group with id " + groupId + " does not exists");
        });

        assertThrows(ResourceNotFoundException.class, () -> {
            groupRepository.findById(groupId);
        });

    }

    @Test
    void testGetAllPostsFromGroupShouldThrowBadRequestException() throws ResourceNotFoundException{

        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        Group group = Group.builder()
                .id(groupId)
                .idAdmin(userId)
                .isPrivate(false)
                .members(new HashSet<>())
                .build();

        given(userRepository.findByUsername(username)).willReturn(user);
        given(groupRepository.findById(groupId)).willReturn(Optional.of(group));;

            assertThrows(BadRequestException.class, () ->{
                postService.getAllPostsFromGroup(groupId, username);
            });

    }

    @Test
    void testDeletePost() throws ResourceNotFoundException {

        Post expectedPost = Post.builder()
                .id(postId)
                .groupId(groupId)
                .userId(userId)
                .hiddenFrom(new HashSet<>())
                .items(new HashSet<>()).
                build();

        when(postRepository.findPostById(postId)).thenReturn(Optional.of(expectedPost));

        postService.deletePost(postId);

    }

    @Test
    void testDeletePostThrowsResourceNotFoundException() throws ResourceNotFoundException {

        Post expectedPost = Post.builder()
                .id(postId)
                .groupId(groupId)
                .userId(userId)
                .hiddenFrom(new HashSet<>())
                .items(new HashSet<>()).
                build();

        given(postRepository.findPostById(postId)).willAnswer(invocation -> {
            throw new ResourceNotFoundException("post with id " + postId + " does not exists");
        });

        assertThrows(ResourceNotFoundException.class, () -> {
            postService.deletePost(postId);
        });

    }

    @Test
    void testUpdatePost() throws ResourceNotFoundException {
        Post foundPost = Post.builder()
                .id(postId)
                .groupId(groupId)
                .userId(userId)
                .hiddenFrom(new HashSet<>())
                .items(new HashSet<>()).
                build();

        PostDTO expectedPostDTO = PostDTO.builder()
                .id(postId)
                .groupId(groupId)
                .userId(userId)
                .hiddenFrom(new HashSet<>())
                .items(new HashSet<>())
                .build();

        when(postRepository.findPostById(postId)).thenReturn(Optional.of(foundPost));

        PostDTO actualPost = postService.updatePost(postId, expectedPostDTO);

        assertThat(actualPost).usingRecursiveComparison().isEqualTo(expectedPostDTO);
        verify(postRepository, times(1)).findPostById(postId);
        verify(postRepository, times(1)).save(foundPost);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    void testUpdatePostThrowsResourceNotFoundException() throws ResourceNotFoundException {
        Post foundPost = Post.builder()
                .id(postId)
                .groupId(groupId)
                .userId(userId)
                .hiddenFrom(new HashSet<>())
                .items(new HashSet<>()).
                build();

        PostDTO expectedPostDTO = PostDTO.builder()
                .id(postId)
                .groupId(groupId)
                .userId(userId)
                .hiddenFrom(new HashSet<>())
                .items(new HashSet<>())
                .build();

        given(postRepository.findPostById(postId)).willAnswer(invocation -> {
            throw new ResourceNotFoundException("post with id " + postId + " does not exists");
        });

        assertThrows(ResourceNotFoundException.class, () -> {
            postService.updatePost(postId, expectedPostDTO);
        });
    }
}