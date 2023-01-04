package com.levi9.socialnetwork.controller;

import com.levi9.socialnetwork.Controller.AddressController;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Address;
import com.levi9.socialnetwork.Service.impl.AddressServiceImpl;
import com.levi9.socialnetwork.dto.AddressDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AddressController.class)
class AddressControllerIntTest {

	@InjectMocks
	private AddressController addressController;

	@MockBean
	private AddressServiceImpl addressService;

	MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	static final Long addressId = 1L;

	@BeforeEach
	public void setup() throws Exception {

		MockitoAnnotations.initMocks(this);

		this.mockMvc = MockMvcBuilders.standaloneSetup(addressController).build();
	}

	@Test
	void itShouldReturnAllAddresses() throws Exception {

		List<Address> listOfAddresses = new ArrayList<>();
		listOfAddresses
				.add(Address.builder().id(1L).country("Serbia").city("Kraljevo").street("Zelenjak").number(1).build());
		listOfAddresses
				.add(Address.builder().id(2L).country("Serbia").city("Kraljevo").street("Zelenjak").number(1).build());
		given(addressService.getAllAddresses()).willReturn(listOfAddresses);

		ResultActions response = mockMvc.perform(get("/api/addresses"));

		response.andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$.size()", is(listOfAddresses.size())));
	}

	@Test
	public void givenAddressIdWhenGetAddressByIdThenReturnAddressObject() throws Exception {

		Address address = Address.builder().id(1L).id(1L).country("Serbia").city("Kraljevo").street("Zelenjak")
				.number(1).build();

		given(addressService.getAddressById(addressId)).willReturn(address);

		ResultActions response = mockMvc.perform(get("/api/addresses/{id}", addressId));

		response.andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$.id", is(address.getId().intValue())))
				.andExpect(jsonPath("$.country", is(address.getCountry())))
				.andExpect(jsonPath("$.city", is(address.getCity())));

	}

	@Test
	public void givenAddressIdWhenGetAddressByIdThenReturnException() throws Exception {

		Address address = Address.builder().id(1L).country("Serbia").city("Kraljevo").street("Zelenjak").number(1)
				.build();

		given(addressService.getAddressById(addressId)).willThrow(ResourceNotFoundException.class);

		MvcResult response = mockMvc.perform(
				get("/api/addresses/{id}", 1L).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
				.andExpect(status().isNotFound()).andReturn();

	}

	@Test
	public void givenUpdatedAddressWhenUpdateAddressThenReturnUpdateAddressObject() throws Exception {

		AddressDTO savedAddress = AddressDTO.builder().id(1L).country("Serbia").city("Kraljevo").street("Zelenjak")
				.number(1).build();

		Address updatedAddress = Address.builder().id(1L).country("Cameroon").city("Ne").street("Travnjak").number(1)
				.build();
		given(addressService.getAddressById(addressId)).willReturn(updatedAddress);
		given(addressService.updateAddress(addressId, updatedAddress)).willReturn(updatedAddress);

		String json = objectMapper.writeValueAsString(savedAddress);
		MvcResult result = mockMvc.perform(put("/api/addresses/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
				.content(json).characterEncoding("utf-8")).andExpect(status().isOk()).andReturn();

	}

	@Test
	public void givenUpdatedAddressWhenUpdateAddressThenReturn404() throws Exception {

		AddressDTO savedAddress = AddressDTO.builder().id(1L).country("Serbia").city("Kraljevo").street("Zelenjak")
				.number(1).build();

		Address updatedAddress = Address.builder().id(1L).country("Cameroon").city("Ne").street("Zelenjak").number(1)
				.build();
		given(addressService.getAddressById(addressId)).willThrow(ResourceNotFoundException.class);
		given(addressService.updateAddress(addressId, updatedAddress)).willThrow(ResourceNotFoundException.class);

		ResultActions response = mockMvc.perform(put("/api/addresses/{id}", addressId)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(savedAddress)));

	}

	@Test
	public void givenAddressIdWhenDeleteAddressThenReturn200() throws Exception {

		Map<String, Boolean> result = new HashMap<>();
		result.put("deleted", Boolean.TRUE);
		
		given(addressService.deleteAddress(addressId))
				.willReturn(result);

		ResultActions response = mockMvc.perform(delete("/api/addresses/{id}", addressId));
		response.andExpect(status().isOk()).andDo(print());
	}

}
