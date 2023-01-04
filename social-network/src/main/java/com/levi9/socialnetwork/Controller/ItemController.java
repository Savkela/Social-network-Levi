package com.levi9.socialnetwork.Controller;

import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Service.ItemService;
import com.levi9.socialnetwork.dto.ItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PutMapping(value = "/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id, @RequestBody ItemDTO itemDTO)
            throws ResourceNotFoundException {

        ItemDTO updatedItem;
        updatedItem = itemService.updateItem(id, itemDTO);
        return new ResponseEntity<>(updatedItem, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) throws ResourceNotFoundException {

        itemService.deleteItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    
}
