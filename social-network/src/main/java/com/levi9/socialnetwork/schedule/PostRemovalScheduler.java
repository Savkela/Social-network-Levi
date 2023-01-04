package com.levi9.socialnetwork.schedule;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.levi9.socialnetwork.Model.Event;
import com.levi9.socialnetwork.Model.Post;
import com.levi9.socialnetwork.Repository.EventRepository;
import com.levi9.socialnetwork.Repository.PostRepository;

@Component
public class PostRemovalScheduler {

    private PostRepository postRepository;

    @Autowired
    public PostRemovalScheduler(PostRepository postRepository) {
        super();
        this.postRepository = postRepository;
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void checkIfStoryExpired() {

        List<Post> expiredPosts = postRepository.getAllExpiredPosts();

        for (Post post : expiredPosts) {
            post.setDeleted(true);
            postRepository.save(post);
        }

    }

}
