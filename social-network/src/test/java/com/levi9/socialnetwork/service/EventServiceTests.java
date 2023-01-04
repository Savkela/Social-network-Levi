package com.levi9.socialnetwork.service;

import com.levi9.socialnetwork.Exception.ResourceExistsException;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Address;
import com.levi9.socialnetwork.Model.Event;
import com.levi9.socialnetwork.Model.Group;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Repository.EventRepository;
import com.levi9.socialnetwork.Repository.UserRepository;
import com.levi9.socialnetwork.Service.impl.EmailServiceImpl;
import com.levi9.socialnetwork.Service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class EventServiceTests {

	@Mock
	private EventRepository eventRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private EventServiceImpl eventService;

	@Mock
	private EmailServiceImpl emailService;

	static final Long eventId = 1L;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

	}

	@Test
	void whenGetEventsItShouldReturnListOfEvents() {

		given(eventRepository.findAll()).willReturn(List.of(new Event(), new Event()));

		assertThat(eventService.getAllEvents()).hasSize(2);
		verify(eventRepository, times(1)).findAll();
	}

	@Test
	void shouldFindAndReturnOneEvent() throws ResourceNotFoundException {

		Event expectedEvent = Event.builder().userId(1L).groupId(1L).id(1L).build();
		when(eventRepository.findById(eventId)).thenReturn(Optional.of(expectedEvent));

		Event actual = eventService.getEventById(1L);

		assertThat(actual).usingRecursiveComparison().isEqualTo(expectedEvent);
		verify(eventRepository, times(1)).findById(1L);
		verifyNoMoreInteractions(eventRepository);
	}

	@Test
	void shouldNotFindEventAndReturnException() {

		given(eventRepository.findById(eventId))
				.willReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class,
				() -> eventService.getEventById(eventId));
	}

	@Test
	void givenEventShouldSaveAndReturnEvent() throws ResourceExistsException {
		Event event = Event.builder().id(1L).userId(1L).build();

		given(eventRepository.existsById(1L))
				.willReturn(false);
		given(eventRepository.save(event))
				.willReturn(event);

		Event returnedEvent = eventService.createEvent(event);
		assertThat(returnedEvent.getId()).isEqualTo(1L);
		assertThat(returnedEvent.getUserId()).isEqualTo(1L);

		event.setId(null);
		returnedEvent = eventService.createEvent(event);
		assertThat(returnedEvent.getUserId()).isEqualTo(1L);

		verify(eventRepository, times(1)).existsById(any());
		verify(eventRepository, times(2)).save(any());
	}

	@Test
	void givenEventShouldThrowResourceExistsException() {
		Event event = Event.builder().id(1L).build();
		given(eventRepository.existsById(1L))
				.willReturn(true);

		assertThrows(ResourceExistsException.class,
				() -> eventService.createEvent(event));
		verify(eventRepository, times(1)).existsById(any());
		verifyNoMoreInteractions(eventRepository);
	}

	@Test
	void givenEventIdAndEventShouldUpdateAndReturnEvent() throws ResourceNotFoundException {
		Event event = Event.builder().id(1L).locationId(2L).build();

		given(eventRepository.findById(1L))
				.willReturn(Optional.of(event));
		given(eventRepository.save(event))
				.willReturn(event);

		Event returnedEvent = eventService.updateEvent(1L, event);
		assertThat(returnedEvent.getId()).isEqualTo(1L);
		assertThat(returnedEvent.getLocationId()).isEqualTo(2L);

		verify(eventRepository, times(1)).findById(any());
		verify(eventRepository, times(1)).save(any());
	}

	@Test
	void givenEventIdAndEventShouldReturnResourceNotFoundException() {
		Event event = Event.builder().id(1L).build();
		given(eventRepository.findById(1L))
				.willReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class,
				() -> eventService.updateEvent(1L, event));
		verify(eventRepository, times(1)).findById(any());
		verifyNoMoreInteractions(eventRepository);
	}

	@Test
	void shouldDeleteAndReturnAllExpiredEvents() {
		List<Event> expiredEvents = List.of(
				Event.builder()
						.id(1L)
						.startDate(LocalDateTime.now().minusDays(2))
						.endDate(LocalDateTime.now().minusDays(1))
						.build(),
				Event.builder()
						.id(3L)
						.startDate(LocalDateTime.now().minusDays(1))
						.endDate(LocalDateTime.now().minusHours(5))
						.build()
		);
		given(eventRepository.getAllExpiredEvents())
				.willReturn(expiredEvents);
		doNothing().when(eventRepository).deleteAll();

		List<Event> returnedExpiredEvents = eventService.deleteAllExpiredEvents();
		assertThat(returnedExpiredEvents).hasSize(2);
		verify(eventRepository, times(1)).getAllExpiredEvents();
		verify(eventRepository, times(1)).deleteAll(any());
		verifyNoMoreInteractions(eventRepository);
	}

	@Test
	void shouldDeleteOneEvent() throws ResourceNotFoundException {

		Event expectedEvent = Event.builder().userId(1L).groupId(1L).id(1L).build();
		given(eventRepository.findById(eventId))
				.willReturn(Optional.of(expectedEvent));
		doNothing().when(eventRepository).delete(expectedEvent);

		eventService.deleteEvent(eventId);

		verify(eventRepository, times(1)).findById(eventId);
		verify(eventRepository, times(1)).delete(expectedEvent);
		verifyNoMoreInteractions(eventRepository);
	}

	@Test
	void shouldDeleteOneEventReturnException() throws ResourceNotFoundException {
		given(eventRepository.findById(eventId))
				.willReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class,
				() -> eventService.deleteEvent(2L));

		verify(eventRepository, times(1)).findById(2L);
		verifyNoMoreInteractions(eventRepository);
	}

	@Test
	void givenEventAddressAndGroupCreateAndSaveEvent() throws ResourceExistsException {
		Event event = Event.builder().id(1L).build();
		Address address = Address.builder().id(2L).build();
		Group group = Group.builder().id(3L).build();
		Event expectedEvent = Event.builder()
				.id(1L).locationId(2L)
				.groupId(3L)
				.build();

		given(eventRepository.existsById(1L))
				.willReturn(false);
		given(eventRepository.save(expectedEvent))
				.willReturn(expectedEvent);

		Event returnedEvent = eventService.createEventInGroup(event, address, group);
		assertThat(returnedEvent.getId()).isEqualTo(1L);
		assertThat(returnedEvent.getLocationId()).isEqualTo(2L);
		assertThat(returnedEvent.getGroupId()).isEqualTo(3L);

		verify(eventRepository, times(1)).existsById(1L);
		verify(eventRepository, times(1)).save(any());
		verifyNoMoreInteractions(eventRepository);
	}

	@Test
	void givenGroupIdShouldReturnEventList() {
		List<Event> eventsInGroup = List.of(
				Event.builder()
						.id(1L)
						.groupId(2L)
						.build(),
				Event.builder()
						.id(3L)
						.groupId(2L)
						.build()
		);
		given(eventRepository.findAllInGroup(2L))
				.willReturn(eventsInGroup);

		List<Event> returnedEvents = eventService.getAllEventsInGroup(2L);
		assertThat(returnedEvents).hasSize(2);
		assertThat(returnedEvents.get(0).getGroupId()).isEqualTo(2L);
		assertThat(returnedEvents.get(1).getGroupId()).isEqualTo(2L);
		verify(eventRepository, times(1)).findAllInGroup(2L);
		verifyNoMoreInteractions(eventRepository);
	}

	@Test
	void shouldNotifyUsersAboutEvents() throws InterruptedException {
		List<Event> eventsForNotify = List.of(
				Event.builder()
						.id(1L)
						.build(),
				Event.builder()
						.id(2L)
						.build()
		);
		List<User> users1 = List.of(
				User.builder()
						.id(1L)
						.build(),
				User.builder()
						.id(2L)
						.build()
		);
		List<User> users2 = List.of(
				User.builder()
						.id(1L)
						.build(),
				User.builder()
						.id(3L)
						.build()
		);
		given(eventRepository.getEventsForNotify())
				.willReturn(eventsForNotify);
		given(userRepository.getUsersOnEvent(1L))
				.willReturn(users1);
		given(userRepository.getUsersOnEvent(2L))
				.willReturn(users2);
		doNothing().when(emailService).sendNotificationAboutEventAsync(any(), any());

		eventService.notifyAllUsersAboutEvent();
		verify(eventRepository, times(1)).getEventsForNotify();
		verify(userRepository, times(2)).getUsersOnEvent(any());
		verify(emailService, times(4)).sendNotificationAboutEventAsync(any(), any());
	}
}
