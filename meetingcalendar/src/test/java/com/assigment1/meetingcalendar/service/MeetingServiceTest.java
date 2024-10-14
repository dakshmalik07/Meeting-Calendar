package com.assigment1.meetingcalendar.service;

import com.assigment1.meetingcalendar.dto.ConflictResponseDTO;
import com.assigment1.meetingcalendar.dto.FreeSlotDTO;
import com.assigment1.meetingcalendar.dto.MeetingRequestDTO;
import com.assigment1.meetingcalendar.entity.Meeting;
import com.assigment1.meetingcalendar.entity.User;
import com.assigment1.meetingcalendar.exception.UserNotFoundException;
import com.assigment1.meetingcalendar.repository.MeetingRepository;
import com.assigment1.meetingcalendar.repository.UserRepository;
import com.assigment1.meetingcalendar.service.MeetingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MeetingServiceTest {

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MeetingService meetingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testBookMeeting_noConflict_success() {
        // Arrange
        MeetingRequestDTO request = new MeetingRequestDTO(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), List.of(1L, 2L));
        Set<User> participants = Set.of(
                User.builder().id(1L).name("User1").build(),
                User.builder().id(2L).name("User2").build()
        );


        when(userRepository.findAllById(request.getParticipantIds())).thenReturn(new ArrayList<>(participants));
        when(meetingRepository.findByParticipantsInAndDate(anySet(), any(LocalDate.class))).thenReturn(Collections.emptyList());

        // Act
        meetingService.bookMeeting(request);

        // Assert
        verify(meetingRepository, times(1)).save(any(Meeting.class));
    }

    @Test
    public void testBookMeeting_withConflict_throwsException() {
        // Arrange
        MeetingRequestDTO request = new MeetingRequestDTO(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), List.of(1L, 2L));
        Set<User> participants = Set.of(
                User.builder().id(1L).name("User1").build(),
                User.builder().id(2L).name("User2").build()
        );
        Meeting existingMeeting = new Meeting(1L, LocalDate.now(), LocalTime.of(9, 30), LocalTime.of(10, 30), participants);

        when(userRepository.findAllById(request.getParticipantIds())).thenReturn(new ArrayList<>(participants));
        when(meetingRepository.findByParticipantsInAndDate(anySet(), any(LocalDate.class))).thenReturn(List.of(existingMeeting));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> meetingService.bookMeeting(request));
    }

    @Test
    public void testFindFreeSlots_returnsAvailableSlots() {
        // Arrange
        FreeSlotDTO request = new FreeSlotDTO(LocalDate.now(), Duration.ofMinutes(30), 1L, 2L);
        User user1 =  User.builder().id(1L).name("User1").build();
        User user2 = User.builder().id(2L).name("User2").build();

        when(userRepository.findById(request.getUser1Id())).thenReturn(Optional.of(user1));
        when(userRepository.findById(request.getUser2Id())).thenReturn(Optional.of(user2));
        when(meetingRepository.findByParticipantsInAndDate(anySet(), any(LocalDate.class))).thenReturn(Collections.emptyList());

        // Act
        List<LocalTime> freeSlots = meetingService.findFreeSlots(request);

        // Assert
        assertFalse(freeSlots.isEmpty());
        assertTrue(freeSlots.contains(LocalTime.of(9, 0)));
    }

    @Test
    public void testFindConflictingParticipants_hasConflict() {
        // Arrange
        MeetingRequestDTO request = new MeetingRequestDTO(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), List.of(1L));
        User user1 = User.builder().id(1L).name("User1").build();
        Meeting existingMeeting = new Meeting(1L, LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), Set.of(user1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(meetingRepository.findByParticipantsInAndDate(anySet(), any(LocalDate.class))).thenReturn(List.of(existingMeeting));

        // Act
        List<ConflictResponseDTO> conflicts = meetingService.findConflictingParticipants(request);

        // Assert
        assertEquals(1, conflicts.size());
        assertTrue(conflicts.get(0).isHasConflict());
    }

    @Test
    public void testFindConflictingParticipants_userNotFound() {
        // Arrange
        MeetingRequestDTO request = new MeetingRequestDTO(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), List.of(1L));

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> meetingService.findConflictingParticipants(request));
    }
}
