package com.levi9.socialnetwork.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.levi9.socialnetwork.Model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findPostById(Long id);

    @Query(value = "Select * from post inner join public.group on post.id_group = :groupId "
            + "INNER JOIN hidden_from hf on post.id = hf.id_post "
            + "WHERE public.group.id = :groupId AND post.private = false and hf.id_user != :userId", nativeQuery = true)
    List<Post> getAllPostsFromGroup(Long groupId, Long userId);

    @Query(value = "SELECT * FROM post\r\n" + "INNER JOIN user_friends on post.id_user = user_friends.id_friend\r\n"
            + "INNER JOIN public.user on public.user.id = user_friends.id_user\r\n"
            + "INNER JOIN hidden_from hf on post.id = hf.id_post\r\n"
            + "WHERE public.user.id = :userId and post.id_group is null and hf.id_user != :userId", nativeQuery = true)
    List<Post> getAllPostsFromFriends(Long userId);

    @Query(value = "SELECT * FROM post\r\n" + "INNER JOIN user_friends on post.id_user = user_friends.id_friend\r\n"
            + "INNER JOIN public.user on public.user.id = user_friends.id_user\r\n"
            + "INNER JOIN public.group on public.group.id = post.id_group\r\n"
            + "INNER JOIN hidden_from hf on post.id = hf.id_post "
            + "WHERE public.user.id = :userId and public.group.private = false and hf.id_user != :userId", nativeQuery = true)
    List<Post> getAllPostsOfMyFriendsFromPublicGroups(Long userId);

    @Query(value = "SELECT * FROM post\r\n" + "INNER JOIN user_friends on post.id_user = user_friends.id_friend\r\n"
            + "INNER JOIN public.user on public.user.id = user_friends.id_user\r\n"
            + "INNER JOIN public.group on public.group.id = post.id_group\r\n"
            + "INNER JOIN public.member on public.group.id = public.member.id_group\r\n"
            + "INNER JOIN hidden_from hf on post.id = hf.id_post "
            + "WHERE public.user.id = :userId and public.member.id_user = :userId and public.group.private = true and hf.id_user != :userId", nativeQuery = true)
    List<Post> getAllPostsOfMyFriendsFromPrivateGroups(Long userId);

    @Query(value = "SELECT * FROM post\r\n"
            + "WHERE (post.created_date  + interval '24 hour') < CURRENT_TIMESTAMP and post.deleted != true ", nativeQuery = true)
    List<Post> getAllExpiredPosts();
}
