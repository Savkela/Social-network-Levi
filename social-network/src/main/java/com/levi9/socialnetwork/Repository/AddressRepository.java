package com.levi9.socialnetwork.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.levi9.socialnetwork.Model.Address;
import com.levi9.socialnetwork.Model.User;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
