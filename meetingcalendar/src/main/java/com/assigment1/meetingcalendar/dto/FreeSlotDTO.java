package com.assigment1.meetingcalendar.dto;

import jakarta.persistence.Entity;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FreeSlotDTO {
    private LocalDate date;
    private Duration duration;
    private Long user1Id;
    private Long user2Id;


}
