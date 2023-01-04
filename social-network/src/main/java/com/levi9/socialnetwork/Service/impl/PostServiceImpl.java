package com.levi9.socialnetwork.Service.impl;

import com.levi9.socialnetwork.Controller.UserController;
import com.levi9.socialnetwork.Exception.BadRequestException;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Group;
import com.levi9.socialnetwork.Model.Post;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Repository.GroupRepository;
import com.levi9.socialnetwork.Repository.PostRepository;
import com.levi9.socialnetwork.Repository.UserRepository;
import com.levi9.socialnetwork.Service.EmailService;
import com.levi9.socialnetwork.Service.PostService;
import com.levi9.socialnetwork.dto.CreatePostDTO;
import com.levi9.socialnetwork.dto.PostDTO;
import com.levi9.socialnetwork.mapper.PostMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class  PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    private Logger logger = org.slf4j.LoggerFactory.getLogger(UserController.class);

    @Autowired
    private EmailService emailService;

    public PostDTO getPostById(Long id) throws ResourceNotFoundException {
        return postRepository.findPostById(id).map(PostMapper::MapEntityToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id " + id + " was not found"));
    }

    public Long createPost(CreatePostDTO postDTO, Long userId){

        Post post = postRepository.save(PostMapper.mapCreateDTOToEntity(postDTO, userId));

        if (postDTO.getGroupId() != null) {
            List<User> notMutedUsers = userRepository.getNotMutedUsers(postDTO.getGroupId());
            for (User user : notMutedUsers) {
                try {
                    emailService.sendNotificaitionAsync(user);
                    return post.getId();
                } catch (Exception e) {
                    logger.info("Error sending email: " + e.getMessage());
                }
            }
        }

        return post.getId();

    }

    public List<Post> getAllPostsFromFriends(Long userId) throws ResourceNotFoundException {
        return postRepository.getAllPostsFromFriends(userId);
    }

    public List<Post> getAllPostsOfMyFriendsFromPublicGroups(Long userId) throws ResourceNotFoundException {
        return postRepository.getAllPostsOfMyFriendsFromPublicGroups(userId);
    }

    public List<Post> getAllPostsOfMyFriendsFromPrivateGroups(Long userId) throws ResourceNotFoundException {
        return postRepository.getAllPostsOfMyFriendsFromPrivateGroups(userId);
    }

    @Transactional
    public PostDTO updatePost(Long id, PostDTO postDTO) throws ResourceNotFoundException {
        Post post = postRepository.findPostById(id).map(searchedPost -> searchedPost)
                .orElseThrow(() -> new ResourceNotFoundException("post with id " + id + " does not exists"));

        post.setText(postDTO.getText());
        post.setPrivate(postDTO.isPrivate());
        post.setCreatedDate(postDTO.getCreatedDate());
        post.setDeleted(postDTO.isDeleted());
        post.setGroupId(postDTO.getGroupId());

        postRepository.save(post);

        return PostMapper.MapEntityToDTO(post);
    }

    @Transactional
    public void deletePost(Long id) throws ResourceNotFoundException {
        Post post = postRepository.findPostById(id)
                .orElseThrow(() -> new ResourceNotFoundException("post with id " + id + " does not exists"));

        post.setDeleted(true);
        postRepository.save(post);
    }
    
    public List<Post> getAllPostsFromGroup(Long groupId,String username) throws ResourceNotFoundException, BadRequestException {
    	
        
        User user = userRepository.findByUsername(username);
    	  Long idLoggedUser = user.getId();
    	  Group group = groupRepository.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("group with id " + groupId + " does not exists"));
    	
        if(!group.containsUser(idLoggedUser)) {
          throw new BadRequestException("User is not member of group.");
        }
    	
    	  return postRepository.getAllPostsFromGroup(groupId,user.getId());
    
    }
}
