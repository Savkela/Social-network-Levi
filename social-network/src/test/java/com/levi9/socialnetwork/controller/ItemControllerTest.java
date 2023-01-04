package com.levi9.socialnetwork.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi9.socialnetwork.Controller.ItemController;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Item;
import com.levi9.socialnetwork.Service.impl.ItemServiceImpl;
import com.levi9.socialnetwork.dto.ItemDTO;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ItemController.class)
class ItemControllerTest {

	@InjectMocks
	private ItemController itemController;

	@MockBean
	private ItemServiceImpl itemService;

	MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	static final Long itemId = 1L;

	@BeforeEach
	public void setup() throws Exception {

		MockitoAnnotations.initMocks(this);

		this.mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
	}

	@Test
	public void givenUpdatedItemWhenUpdateItemThenReturnUpdateItemObject() throws Exception {

		ItemDTO savedItem = ItemDTO.builder().id(1L).link("https://www.slikomania.rs/fotky6509/fotos/CWFTR026.jpg")
				.build();

		ItemDTO updatedItem = ItemDTO.builder().id(1L)
				.link("http://www.10naj.com/wp-content/uploads/2016/08/zvezdananoc_.jpg").build();

		given(itemService.getItemById(itemId)).willReturn(savedItem);
		given(itemService.updateItem(itemId, updatedItem)).willReturn(updatedItem);

		String json = objectMapper.writeValueAsString(savedItem);
		MvcResult result = mockMvc.perform(put("/api/items/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
				.content(json).characterEncoding("utf-8")).andExpect(status().isOk()).andReturn();
	}

	@Test
	public void givenUpdatedItemWhenUpdateItemThenReturn404() throws Exception {

		ItemDTO savedItem = ItemDTO.builder().id(1L).link("https://www.slikomania.rs/fotky6509/fotos/CWFTR026.jpg")
				.build();

		ItemDTO updatedItem = ItemDTO.builder().id(1L)
				.link("http://www.10naj.com/wp-content/uploads/2016/08/zvezdananoc_.jpg").build();
		given(itemService.getItemById(itemId)).willThrow(ResourceNotFoundException.class);
		given(itemService.updateItem(itemId, updatedItem)).willThrow(ResourceNotFoundException.class);

		ResultActions response = mockMvc.perform(put("/api/items/{id}", itemId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(savedItem)));
	}

	@Test
	public void givenItemIdWhenDeleteItemThenReturn200() throws Exception {

		willDoNothing().given(itemService).deleteItem(itemId);

		ResultActions response = mockMvc.perform(delete("/api/items/{id}", itemId));
		response.andExpect(status().isNoContent()).andDo(print());
	}

}
