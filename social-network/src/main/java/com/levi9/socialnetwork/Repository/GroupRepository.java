package com.levi9.socialnetwork.Repository;

import com.levi9.socialnetwork.Model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    @Modifying
    @Query(value = "delete from member_event where id_user = :userId and id_group = :groupId", nativeQuery = true)
    void deleteMemberEvents(Long userId, Long groupId);

    @Modifying
    @Transactional
    @Query(value = "delete from member_event where member_event.id_user = :userId and member_event.id_group = :groupId", nativeQuery = true)
    void removeMembersFromEvents(Long groupId, Long userId);
}
