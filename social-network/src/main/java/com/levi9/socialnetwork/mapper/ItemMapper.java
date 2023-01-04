package com.levi9.socialnetwork.mapper;

import com.levi9.socialnetwork.Model.Item;
import com.levi9.socialnetwork.dto.ItemDTO;

public class ItemMapper {

    public static ItemDTO mapEntityToDTO(Item item){
        return new ItemDTO(item.getId(), item.getLink());
    }

    public static Item mapDTOToEntity(ItemDTO item){
        return new Item(item.getId(), item.getLink());

    }
}
