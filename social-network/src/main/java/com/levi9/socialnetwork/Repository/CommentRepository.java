package com.levi9.socialnetwork.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.levi9.socialnetwork.Model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "Select * from comment where id_post = :postId", nativeQuery = true)
    List<Comment> getCommentsByPost(Long postId);

    @Query(value = "Select * from comment where id_replied_to = :commentId", nativeQuery = true)
    List<Comment> getRepliesByComment(Long commentId);

}
