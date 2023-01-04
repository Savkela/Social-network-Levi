package com.levi9.socialnetwork.Service;

import com.levi9.socialnetwork.Exception.ResourceExistsException;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Address;
import com.levi9.socialnetwork.Model.Event;
import com.levi9.socialnetwork.Model.Group;

import java.util.List;

public interface EventService {
    List<Event> getAllEvents();

    Event getEventById(Long eventId) throws ResourceNotFoundException;

    Event createEvent(Event event) throws ResourceExistsException;

    Event updateEvent(Long eventId, Event eventDetails) throws ResourceNotFoundException;

    List<Event> deleteAllExpiredEvents() throws ResourceNotFoundException;

    void deleteEvent(Long eventId) throws ResourceNotFoundException;

    Event createEventInGroup(Event event, Address address, Group group) throws ResourceExistsException;

    List<Event> getAllEventsInGroup(Long groupId);
}
