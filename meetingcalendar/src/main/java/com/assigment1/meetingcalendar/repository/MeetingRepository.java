package com.assigment1.meetingcalendar.repository;

import com.assigment1.meetingcalendar.entity.Meeting;
import com.assigment1.meetingcalendar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByParticipantsInAndDate(Set<User> users, LocalDate date);
}
