package com.levi9.socialnetwork.service;

import com.levi9.socialnetwork.Exception.BadRequestException;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Comment;
import com.levi9.socialnetwork.Model.Post;
import com.levi9.socialnetwork.Repository.CommentRepository;
import com.levi9.socialnetwork.Repository.PostRepository;
import com.levi9.socialnetwork.Service.impl.CommentServiceImpl;
import com.levi9.socialnetwork.dto.CommentDTO;
import com.levi9.socialnetwork.dto.ReplyDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
class CommentServiceTests {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllComments() {
        shouldReturnCommentList();
    }

    @Test
    void testReplyToComment() throws ResourceNotFoundException {
        givenReplyDTOShouldSaveAndReturnComment();
        givenReplyDTOShouldThrowBadRequestException();
        givenReplyDTOShouldThrowResourceNotFoundException();
    }

    @Test
    void testGetCommentsByPost() throws ResourceNotFoundException {
        givenPostIdReturnCommentList();
        givenPostIdThrowResourceNotFoundException();
    }

    @Test
    void testGetRepliesByComment() throws ResourceNotFoundException {
        givenCommentIdReturnReplyList();
        givenCommentIdThrowResourceNotFoundException();
    }

    @Test
    void testGetCommentById() throws ResourceNotFoundException {
        givenCommentIdShouldReturnComment();
        givenCommentIdShouldThrowResourceNotFoundGet();
    }

    @Test
    void testCreateComment() {
        givenCommentDTOShouldSaveAndReturnComment();
    }

    @Test
    void testUpdateComment() throws ResourceNotFoundException {
        givenCommentIdAndCommentDTOUpdateComment();
        givenCommentIdAndCommentDTOShouldThrowResourceNotFound();
    }

    @Test
    void testDeleteComment() throws ResourceNotFoundException {
        givenCommentIdShouldLogicallyDeleteAndReturnComment();
        givenCommentIdShouldThrowResourceNotFoundDelete();
    }

    void shouldReturnCommentList() {
        List<Comment> commentList = List.of(
                Comment.builder().id(1L).build(),
                Comment.builder().id(2L).build()
        );

        given(commentRepository.findAll())
                .willReturn(commentList);

        List<Comment> returnedComments = commentService.getAllComments();
        assertThat(returnedComments).hasSize(2);
        assertThat(returnedComments.get(0).getId()).isEqualTo(1L);
        assertThat(returnedComments.get(1).getId()).isEqualTo(2L);
    }

    void givenReplyDTOShouldSaveAndReturnComment() throws ResourceNotFoundException {
        Post post = Post.builder()
                .id(1L)
                .deleted(false)
                .build();
        ReplyDTO replyDTO = ReplyDTO.builder()
                .idPost(1L)
                .idRepliedTo(1L)
                .build();
        Comment comment = Comment.builder()
                .id(2L)
                .idPost(1L)
                .idRepliedTo(1L)
                .build();

        given(postRepository.findById(1L))
                .willReturn(Optional.ofNullable(post));
        given(commentRepository.existsById(1L))
                .willReturn(true);
        given(commentRepository.save(new Comment(replyDTO)))
                .willReturn(comment);

        Comment returnedComment = commentService.replyToComment(replyDTO);
        assertThat(returnedComment.getId()).isEqualTo(2L);
    }

    void givenReplyDTOShouldThrowBadRequestException() {
        Post post = Post.builder()
                .id(1L)
                .deleted(true)
                .build();
        ReplyDTO replyDTO = ReplyDTO.builder()
                .idPost(1L)
                .idRepliedTo(1L)
                .build();

        given(postRepository.findById(1L))
                .willReturn(Optional.ofNullable(post));

        assertThrows(BadRequestException.class,
                () -> commentService.replyToComment(replyDTO));
    }

    void givenReplyDTOShouldThrowResourceNotFoundException() {
        ReplyDTO replyDTO = ReplyDTO.builder()
                .idPost(1L)
                .idRepliedTo(1L)
                .build();

        given(postRepository.findById(1L))
                .willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commentService.replyToComment(replyDTO));


        replyDTO.setIdPost(2L);
        Post post = Post.builder()
                .id(2L)
                .deleted(false)
                .build();

        given(postRepository.findById(2L))
                .willReturn(Optional.ofNullable(post));
        given(commentRepository.existsById(1L))
                .willReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> commentService.replyToComment(replyDTO));

    }

    void givenPostIdReturnCommentList() throws ResourceNotFoundException {
        List<Comment> commentList = List.of(
                Comment.builder().id(1L).build(),
                Comment.builder().id(2L).build()
        );

        given(postRepository.existsById(1L))
                .willReturn(true);
        given(commentRepository.getCommentsByPost(1L))
                .willReturn(commentList);

        List<Comment> returnedComments = commentService.getCommentsByPost(1L);
        assertThat(returnedComments).hasSize(2);
        assertThat(returnedComments.get(0).getId()).isEqualTo(1L);
        assertThat(returnedComments.get(1).getId()).isEqualTo(2L);
    }

    void givenPostIdThrowResourceNotFoundException() {
        given(postRepository.existsById(1L))
                .willReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> commentService.getCommentsByPost(1L));
    }

    void givenCommentIdReturnReplyList() throws ResourceNotFoundException {
        List<Comment> commentList = List.of(
                Comment.builder().id(2L).build(),
                Comment.builder().id(3L).build()
        );

        given(commentRepository.existsById(1L))
                .willReturn(true);
        given(commentRepository.getRepliesByComment(1L))
                .willReturn(commentList);

        List<Comment> returnedComments = commentService.getRepliesByComment(1L);
        assertThat(returnedComments).hasSize(2);
        assertThat(returnedComments.get(0).getId()).isEqualTo(2L);
        assertThat(returnedComments.get(1).getId()).isEqualTo(3L);
    }

    void givenCommentIdThrowResourceNotFoundException() {
        given(commentRepository.existsById(1L))
                .willReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> commentService.getRepliesByComment(1L));
    }

    void givenCommentIdShouldReturnComment() throws ResourceNotFoundException {
        Comment comment = Comment.builder().id(1L).build();
        given(commentRepository.findById(1L))
                .willReturn(Optional.ofNullable(comment));

        Comment returnedComment = commentService.getCommentById(1L);
        assertThat(returnedComment.getId()).isEqualTo(1L);
    }
    void givenCommentIdShouldThrowResourceNotFoundGet() {
        given(commentRepository.findById(1L))
                .willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commentService.getCommentById(1L));

    }

    void givenCommentDTOShouldSaveAndReturnComment() {
        CommentDTO commentDTO = CommentDTO.builder()
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .build();

        given(commentRepository.save(new Comment(commentDTO)))
                .willReturn(comment);

        Comment returnedComment = commentService.createComment(commentDTO);
        assertThat(returnedComment.getId()).isEqualTo(1L);
    }

    void givenCommentIdAndCommentDTOUpdateComment() throws ResourceNotFoundException {
        CommentDTO commentDTO = CommentDTO.builder()
                .text("Lorem ipsum")
                .deleted(false)
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .text("Lorem")
                .deleted(true)
                .build();
        Comment updatedComment = Comment.builder()
                .id(1L)
                .text("Lorem ipsum")
                .deleted(false)
                .build();

        given(commentRepository.findById(1L))
                .willReturn(Optional.ofNullable(comment));
        given(commentRepository.save(updatedComment))
                .willReturn(updatedComment);

        Comment returnedComment = commentService.updateComment(1L, commentDTO);
        assertThat(returnedComment.getId()).isEqualTo(1L);
        assertThat(returnedComment.getText()).isEqualTo("Lorem ipsum");
        assertThat(returnedComment.isDeleted()).isFalse();
    }

    void givenCommentIdAndCommentDTOShouldThrowResourceNotFound() {
        given(commentRepository.findById(1L))
                .willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commentService.updateComment(1L, new CommentDTO()));
    }

    void givenCommentIdShouldLogicallyDeleteAndReturnComment() throws ResourceNotFoundException {
        Comment comment = Comment.builder()
                .id(1L)
                .deleted(false)
                .build();
        Comment deletedComment = Comment.builder()
                .id(1L)
                .deleted(true)
                .build();

        given(commentRepository.findById(1L))
                .willReturn(Optional.ofNullable(comment));
        given(commentRepository.save(deletedComment))
                .willReturn(deletedComment);

        Comment returnedComment = commentService.deleteComment(1L);
        assertThat(returnedComment.getId()).isEqualTo(1L);
        assertThat(returnedComment.isDeleted()).isTrue();
    }

    void givenCommentIdShouldThrowResourceNotFoundDelete() {
        given(commentRepository.findById(1L))
                .willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commentService.deleteComment(1L));
    }
}
