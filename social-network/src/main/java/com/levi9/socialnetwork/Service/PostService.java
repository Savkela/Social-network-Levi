package com.levi9.socialnetwork.Service;

import com.levi9.socialnetwork.Exception.BadRequestException;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Post;
import com.levi9.socialnetwork.dto.CreatePostDTO;
import com.levi9.socialnetwork.dto.PostDTO;

import java.util.List;

public interface PostService {

    public PostDTO getPostById(Long id) throws ResourceNotFoundException;

    public Long createPost(CreatePostDTO postDTO, Long userId);

    public List<Post> getAllPostsFromFriends(Long userId) throws ResourceNotFoundException;

    public List<Post> getAllPostsOfMyFriendsFromPublicGroups(Long userId) throws ResourceNotFoundException;

    public List<Post> getAllPostsOfMyFriendsFromPrivateGroups(Long userId) throws ResourceNotFoundException;

    public PostDTO updatePost(Long id, PostDTO postDTO) throws ResourceNotFoundException;

    public void deletePost(Long id) throws ResourceNotFoundException;

    public List<Post> getAllPostsFromGroup(Long groupId,String username) throws ResourceNotFoundException, BadRequestException;
}
