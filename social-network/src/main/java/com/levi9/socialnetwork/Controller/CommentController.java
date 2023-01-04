package com.levi9.socialnetwork.Controller;

import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Comment;
import com.levi9.socialnetwork.Service.CommentService;
import com.levi9.socialnetwork.dto.CommentDTO;
import com.levi9.socialnetwork.dto.ReplyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllComments() {

        List<Comment> comments = commentService.getAllComments();
        List<CommentDTO> commentDTOS = comments.stream().map(CommentDTO::new).toList();
        return ResponseEntity.ok().body(commentDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getComment(@PathVariable(value = "id") Long commentId)
            throws ResourceNotFoundException {

        Comment comment = commentService.getCommentById(commentId);
        return ResponseEntity.ok().body(new CommentDTO(comment));

    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPost(@PathVariable(value = "postId") Long postId)
            throws ResourceNotFoundException {

        List<Comment> comments = commentService.getCommentsByPost(postId);
        List<CommentDTO> commentDTOS = comments.stream().map(CommentDTO::new).toList();
        return ResponseEntity.ok().body(commentDTOS);
    }

    @GetMapping("{commentId}/reply")
    public ResponseEntity<List<ReplyDTO>> getRepliesByComment(@PathVariable(value = "commentId") Long commentId)
            throws ResourceNotFoundException {

        List<Comment> comments = commentService.getRepliesByComment(commentId);
        List<ReplyDTO> replyDTOS = comments.stream().map(ReplyDTO::new).toList();
        return ResponseEntity.ok().body(replyDTOS);
    }

    @PostMapping("/reply")
    public ResponseEntity<ReplyDTO> replyToComment(@RequestBody ReplyDTO replyDTO) throws ResourceNotFoundException {
        Comment reply = commentService.replyToComment(replyDTO);
        return ResponseEntity.ok().body(new ReplyDTO(reply));
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {

        Comment comment = commentService.createComment(commentDTO);
        return ResponseEntity.ok().body(new CommentDTO(comment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable(value = "id") Long commentId,
            @RequestBody CommentDTO commentDTO) throws ResourceNotFoundException {

        Comment updatedComment = commentService.updateComment(commentId, commentDTO);
        return ResponseEntity.ok().body(new CommentDTO(updatedComment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommentDTO> deleteComment(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {

        Comment comment = commentService.deleteComment(id);
        return ResponseEntity.ok().body(new CommentDTO(comment));
    }
}
