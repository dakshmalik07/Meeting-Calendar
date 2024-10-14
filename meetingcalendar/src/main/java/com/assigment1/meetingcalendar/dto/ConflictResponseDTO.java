package com.assigment1.meetingcalendar.dto;


import jakarta.persistence.Entity;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ConflictResponseDTO {
    private Long userId;
    private boolean hasConflict;

    // Constructor, getters, and setters
}

