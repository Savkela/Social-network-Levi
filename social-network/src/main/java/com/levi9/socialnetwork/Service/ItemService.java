package com.levi9.socialnetwork.Service;

import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.dto.ItemDTO;

public interface ItemService {
    public ItemDTO getItemById(Long id) throws ResourceNotFoundException;

    public Long createItem(ItemDTO itemDTO);

    public ItemDTO updateItem(Long id, ItemDTO itemDTO) throws ResourceNotFoundException;

    public void deleteItem(Long id) throws ResourceNotFoundException;
}
