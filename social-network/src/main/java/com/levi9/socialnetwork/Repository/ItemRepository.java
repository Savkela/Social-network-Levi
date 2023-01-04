package com.levi9.socialnetwork.Repository;

import com.levi9.socialnetwork.Model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findItemById(Long id);

    void deleteById(Long id);
}
