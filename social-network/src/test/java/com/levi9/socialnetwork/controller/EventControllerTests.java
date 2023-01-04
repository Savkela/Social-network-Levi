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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi9.socialnetwork.Controller.EventController;
import com.levi9.socialnetwork.Exception.CustomExceptionHandler;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Event;
import com.levi9.socialnetwork.Service.impl.EventServiceImpl;
import com.levi9.socialnetwork.dto.AddressDTO;
import com.levi9.socialnetwork.dto.EventDTO;

@RunWith(SpringRunner.class)
@WebMvcTest(EventController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(CustomExceptionHandler.class)
class EventControllerTests {

	@InjectMocks
	private EventController eventController;

	@MockBean
	private EventServiceImpl eventService;

	MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	static final Long eventId = 1L;

	@BeforeEach
	public void setup() throws Exception {

		MockitoAnnotations.initMocks(this);

		this.mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
	}

	@Test
	void itShouldReturnAllEvents() throws Exception {

		List<Event> listOfEvents = new ArrayList<>();
		listOfEvents.add(Event.builder().id(1L).userId(1l).groupId(1L).build());
		listOfEvents.add(Event.builder().id(2L).userId(1l).groupId(1L).build());
		given(eventService.getAllEvents()).willReturn(listOfEvents);

		ResultActions response = mockMvc.perform(get("/api/events"));

		response.andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$.size()", is(listOfEvents.size())));
	}

	@Test
	public void givenEventIdWhenGetEventByIdThenReturnEventObject() throws Exception {

		Event event = Event.builder().id(1L).userId(1l).groupId(1L).build();

		given(eventService.getEventById(eventId)).willReturn(event);

		ResultActions response = mockMvc.perform(get("/api/events/{id}", eventId));

		response.andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$.id", is(event.getId().intValue())))
				.andExpect(jsonPath("$.groupId", is(event.getGroupId().intValue())))
				.andExpect(jsonPath("$.userId", is(event.getUserId().intValue())));

	}

	@Test
	public void givenEventIdWhenGetEventByIdThenReturnException() throws Exception {

		Event event = Event.builder().id(2L).userId(1l).groupId(1L).build();

		given(eventService.getEventById(eventId)).willThrow(ResourceNotFoundException.class);

		MvcResult response = mockMvc
				.perform(get("/api/events/{id}", 1L).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
				.andExpect(status().isNotFound()).andReturn();

	}

	@Test
	public void givenUpdatedEventWhenUpdateEventThenReturnUpdateEventObject() throws Exception {

		EventDTO savedEvent = EventDTO.builder().id(1L).userId(1l).groupId(1L)
				.location(AddressDTO.builder().id(1L).country("Serbia").city("Novi Sad").build()).build();

		Event updatedEvent = Event.builder().id(1L).userId(1l).groupId(2L).build();
		given(eventService.getEventById(eventId)).willReturn(updatedEvent);
		given(eventService.updateEvent(eventId, updatedEvent)).willReturn(updatedEvent);

		String json = objectMapper.writeValueAsString(savedEvent);
		MvcResult result = mockMvc.perform(put("/api/events/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
				.content(json).characterEncoding("utf-8")).andExpect(status().isOk()).andReturn();

	}

	@Test
	public void givenUpdatedEventWhenUpdateEventThenReturn404() throws Exception {

		EventDTO savedEvent = EventDTO.builder().id(2L).userId(1l).groupId(1L)
				.location(AddressDTO.builder().id(1L).country("Serbia").city("Novi Sad").build()).build();

		Event updatedEvent = Event.builder().id(2L).userId(1l).groupId(2L).build();
		given(eventService.getEventById(eventId)).willThrow(ResourceNotFoundException.class);
		given(eventService.updateEvent(eventId, updatedEvent)).willThrow(ResourceNotFoundException.class);

		ResultActions response = mockMvc.perform(put("/api/events/{id}", eventId)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(savedEvent)));

	}

	@Test
	public void givenEventIdWhenDeleteEventThenReturn200() throws Exception {

		willDoNothing().given(eventService).deleteEvent(eventId);

		ResultActions response = mockMvc.perform(delete("/api/events/{id}", eventId));
		response.andExpect(status().isOk()).andDo(print());
	}

}
