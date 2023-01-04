package com.levi9.socialnetwork.Repository;

import com.levi9.socialnetwork.Model.MuteGroup;
import com.levi9.socialnetwork.Model.MuteGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MuteGroupRepository extends JpaRepository<MuteGroup, MuteGroupId> {
}
