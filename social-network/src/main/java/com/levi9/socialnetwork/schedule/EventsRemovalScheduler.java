package com.levi9.socialnetwork.schedule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.levi9.socialnetwork.Model.Event;
import com.levi9.socialnetwork.Repository.EventRepository;

@Component
public class EventsRemovalScheduler {

    private EventRepository eventRepository;

    @Autowired
    public EventsRemovalScheduler(EventRepository eventRepository) {
        super();
        this.eventRepository = eventRepository;
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void removeEvent() {
        List<Event> expiredEvents = eventRepository.getAllExpiredEvents();

        for (Event e : expiredEvents) {
            eventRepository.delete(e);
        }

    }

}
