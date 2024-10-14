package com.assigment1.meetingcalendar.service;

import com.assigment1.meetingcalendar.dto.ConflictResponseDTO;
import com.assigment1.meetingcalendar.dto.FreeSlotDTO;
import com.assigment1.meetingcalendar.dto.MeetingRequestDTO;
import com.assigment1.meetingcalendar.entity.Meeting;
import com.assigment1.meetingcalendar.entity.User;
import com.assigment1.meetingcalendar.exception.UserNotFoundException;
import com.assigment1.meetingcalendar.repository.MeetingRepository;
import com.assigment1.meetingcalendar.repository.UserRepository;
import com.assigment1.meetingcalendar.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private UserRepository userRepository;

    public void bookMeeting(MeetingRequestDTO request) {
        LocalDate date = request.getDate();
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = request.getEndTime();

        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        Set<User> participants = userRepository.findAllById(request.getParticipantIds()).stream().collect(Collectors.toSet());

        boolean hasConflict = participants.stream().anyMatch(user ->
                meetingRepository.findByParticipantsInAndDate(Set.of(user), date)
                        .stream()
                        .anyMatch(meeting -> isTimeOverlap(meeting, startTime, endTime) ||
                                (meeting.getStartTime().equals(startTime) && meeting.getEndTime().equals(endTime)))
        );

        if (hasConflict) {
            throw new IllegalArgumentException("Meeting conflicts with existing meetings.");
        }

        Meeting meeting = new Meeting();
        meeting.setDate(date);
        meeting.setStartTime(startTime);
        meeting.setEndTime(endTime);
        meeting.setParticipants(participants);
        meetingRepository.save(meeting);
    }


    public List<LocalTime> findFreeSlots(FreeSlotDTO request) {
        LocalDate date = request.getDate();
        Duration duration = request.getDuration();

        // Enhanced error handling
        User user1 = userRepository.findById(request.getUser1Id())
                .orElseThrow(() -> new UserNotFoundException("User with ID " + request.getUser1Id() + " not found."));
        User user2 = userRepository.findById(request.getUser2Id())
                .orElseThrow(() -> new UserNotFoundException("User with ID " + request.getUser2Id() + " not found."));

        List<Meeting> meetingsUser1 = meetingRepository.findByParticipantsInAndDate(Set.of(user1), date);
        List<Meeting> meetingsUser2 = meetingRepository.findByParticipantsInAndDate(Set.of(user2), date);

        return TimeUtil.findFreeSlots(meetingsUser1, meetingsUser2, duration);
    }
    public List<ConflictResponseDTO> findConflictingParticipants(MeetingRequestDTO request) {
        return request.getParticipantIds().stream()
                .map(userId -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
                    boolean hasConflict = meetingRepository.findByParticipantsInAndDate(Set.of(user), request.getDate())
                            .stream()
                            .anyMatch(meeting -> isTimeOverlap(meeting, request.getStartTime(), request.getEndTime()));
                    return new ConflictResponseDTO(userId, hasConflict);
                }).collect(Collectors.toList());
    }

    private boolean isTimeOverlap(Meeting meeting, LocalTime startTime, LocalTime endTime) {
        return !(meeting.getEndTime().isBefore(startTime) || meeting.getStartTime().isAfter(endTime));
    }
}
