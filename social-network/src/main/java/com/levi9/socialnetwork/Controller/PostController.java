package com.levi9.socialnetwork.Controller;

import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Group;
import com.levi9.socialnetwork.Model.Post;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Service.GroupService;
import com.levi9.socialnetwork.Service.PostService;
import com.levi9.socialnetwork.Service.UserService;
import com.levi9.socialnetwork.dto.CreatePostDTO;
import com.levi9.socialnetwork.dto.PostDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<PostDTO> getOne(@PathVariable Long id){

        PostDTO postDTO;

        try{
            postDTO = postService.getPostById(id);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(postDTO, HttpStatus.OK);
    }


    @GetMapping(value = "/friend/{userId}")
    public ResponseEntity<List<Post>> getAllPostsFromFriends(@PathVariable Long userId) throws ResourceNotFoundException {

        return new ResponseEntity<>(postService.getAllPostsFromFriends(userId), HttpStatus.OK);
    }


    @GetMapping(value = "/friend/{userId}/public-groups")
    public ResponseEntity<List<Post>> getAllPostsOfMyFriendsFromPublicGroups(@PathVariable Long userId) throws ResourceNotFoundException {

        return new ResponseEntity<>(postService.getAllPostsOfMyFriendsFromPublicGroups(userId), HttpStatus.OK);
    }

    @GetMapping(value = "/friend/{userId}/private-groups")
    public ResponseEntity<List<Post>> getAllPostsOfMyFriendsFromPrivateGroups(@PathVariable Long userId) throws ResourceNotFoundException {

        return new ResponseEntity<>(postService.getAllPostsOfMyFriendsFromPrivateGroups(userId), HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<Long> createPost(@RequestBody CreatePostDTO postDTO, Principal principal) throws ResourceNotFoundException {

        User user = userService.findUserByUsername(principal.getName());

        if(user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if(postDTO.getGroupId() != null){
            Group group = groupService.getGroupById(postDTO.getGroupId());
            if(!group.containsUser(user.getId())){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        Long id;
        try{
            id = postService.createPost(postDTO, user.getId());

        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUriString();

        return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.LOCATION, location).body(id);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id, @RequestBody PostDTO postDTO, Principal principal) throws ResourceNotFoundException {

        User user = userService.findUserByUsername(principal.getName());
        PostDTO post = postService.getPostById(id);

        if (!user.getId().equals(post.getUserId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        PostDTO updatedPost;

        try {
            updatedPost = postService.updatePost(id, postDTO);
        } catch (ResourceNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Principal principal) throws ResourceNotFoundException {

        PostDTO postDTO = postService.getPostById(id);
        User user = userService.findUserByUsername(principal.getName());

        if (!user.getId().equals(postDTO.getUserId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            postService.deletePost(id);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}