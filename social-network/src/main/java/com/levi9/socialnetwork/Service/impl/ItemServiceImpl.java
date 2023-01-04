package com.levi9.socialnetwork.Service.impl;

import java.util.Arrays;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Item;
import com.levi9.socialnetwork.Repository.ItemRepository;
import com.levi9.socialnetwork.Service.ItemService;
import com.levi9.socialnetwork.dto.ItemDTO;
import com.levi9.socialnetwork.mapper.ItemMapper;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RestTemplate restTemplate;

    public ItemDTO getItemById(Long id) throws ResourceNotFoundException {

        ItemDTO item = itemRepository.findItemById(id).map(ItemMapper::mapEntityToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Item with id " + id + "was not found"));

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("API_KEY", "aec093c2-c981-44f9-9a4a-365ad1d2f05e");
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        String link = restTemplate
                .exchange("http://localhost:8762/storage/api/storage/" + id, HttpMethod.GET, entity, String.class)
                .getBody();

        item.setLink(link);

        return item;
    }

    public Long createItem(ItemDTO itemDTO) {
        return itemRepository.save(ItemMapper.mapDTOToEntity(itemDTO)).getId();
    }

    @Transactional
    public ItemDTO updateItem(Long id, ItemDTO itemDTO) throws ResourceNotFoundException {
        Item item = itemRepository.findItemById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item with id " + id + " was not found"));
        
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("API_KEY", "aec093c2-c981-44f9-9a4a-365ad1d2f05e");
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        String link = restTemplate
                .exchange("http://localhost:8762/storage/api/storage/" + id, HttpMethod.GET, entity, String.class)
                .getBody();

        item.setLink(link);

        item = itemRepository.save(item);

        return ItemMapper.mapEntityToDTO(item);
    }

    @Transactional
    public void deleteItem(Long id) throws ResourceNotFoundException {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item with id " + id + " was not found");
        }

        itemRepository.deleteById(id);
    }
}
