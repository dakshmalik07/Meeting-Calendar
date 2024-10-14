package com.assigment1.meetingcalendar.dto;

import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MeetingRequestDTO {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<Long> participantIds;


}
