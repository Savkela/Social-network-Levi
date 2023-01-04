package com.levi9.socialnetwork.Model;

import com.levi9.socialnetwork.dto.EventDTO;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "event", schema = "public")
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "id_location")
    private Long locationId;

    @Column(name = "id_user")
    private Long userId;

    @Column(name = "id_group")
    private Long groupId;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, targetEntity = Member.class)
    @JoinTable(name = "member_event", joinColumns = @JoinColumn(name = "id_event", referencedColumnName = "id", nullable = false, updatable = false), inverseJoinColumns = {
            @JoinColumn(name = "id_user", referencedColumnName = "id_user"),
            @JoinColumn(name = "id_group", referencedColumnName = "id_group") })
    private Set<Member> memberUsers;

    public Event(EventDTO eventDTO) {
        this.id = eventDTO.getId();
        this.locationId = eventDTO.getLocation().getId();
        this.userId = eventDTO.getUserId();
        this.groupId = eventDTO.getGroupId();
        this.startDate = eventDTO.getStartDate();
        this.endDate = eventDTO.getEndDate();
    }

    public Event(Long id, Long locationId, Long userId, Long groupId, LocalDateTime startDate, LocalDateTime endDate,
            Set<Member> memberUsers) {
        super();
        this.id = id;
        this.locationId = locationId;
        this.userId = userId;
        this.groupId = groupId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.memberUsers = memberUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Event event = (Event) o;
        return id != null && Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
