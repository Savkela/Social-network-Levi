package com.levi9.socialnetwork.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.levi9.socialnetwork.Model.Comment;
import com.levi9.socialnetwork.Model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

	  @Query(value = "Select * from user WHERE user.id = :userId", nativeQuery = true)
	  User findUsersInGroup(Long userId);
	
    @Query(value = "SELECT * FROM public.user INNER JOIN member_event ON public.user.id = member_event.id_user "
            + "WHERE member_event.id_event = :idEvent", nativeQuery = true)
    List<User> getUsersOnEvent(Long idEvent);

    @Query(value = "Select * from public.user \r\n" + "INNER JOIN mute_group on public.user.id = mute_group.id_user\r\n"
            + "WHERE is_permanent = false and mute_group.end_of_mute < CURRENT_TIMESTAMP and id_group = :groupId", nativeQuery = true)
    List<User> getNotMutedUsers(Long groupId);

}
