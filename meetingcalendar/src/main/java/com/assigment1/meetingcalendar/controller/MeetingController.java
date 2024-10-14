package com.assigment1.meetingcalendar.controller;

import com.assigment1.meetingcalendar.dto.ConflictResponseDTO;
import com.assigment1.meetingcalendar.dto.FreeSlotDTO;
import com.assigment1.meetingcalendar.dto.MeetingRequestDTO;
import com.assigment1.meetingcalendar.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @PostMapping("/book")
    public ResponseEntity<String> bookMeeting(@RequestBody MeetingRequestDTO request) {
        try {
            meetingService.bookMeeting(request);
            return ResponseEntity.ok("Meeting booked successfully.");
        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/free-slots")
    public ResponseEntity<List<LocalTime>> findFreeSlots(@RequestBody FreeSlotDTO request) {
        return ResponseEntity.ok(meetingService.findFreeSlots(request));
    }

    @PostMapping("/conflicts")
    public ResponseEntity<List<ConflictResponseDTO>> findConflictingParticipants(@RequestBody MeetingRequestDTO request) {
        return ResponseEntity.ok(meetingService.findConflictingParticipants(request));
    }
}
