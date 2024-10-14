package com.assigment1.meetingcalendar.util;

import com.assigment1.meetingcalendar.entity.Meeting;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimeUtil {

    public static List<LocalTime> findFreeSlots(List<Meeting> meetingsUser1, List<Meeting> meetingsUser2, Duration duration) {
        List<LocalTime> freeSlots = new ArrayList<>();
        LocalTime workingStart = LocalTime.of(9, 0);
        LocalTime workingEnd = LocalTime.of(18, 0);

        LocalTime currentStart = workingStart;
        while (currentStart.plus(duration).isBefore(workingEnd)) {
            LocalTime slotEnd = currentStart.plus(duration);
            final LocalTime slotStart = currentStart;
            boolean slotAvailable = meetingsUser1.stream().noneMatch(meeting ->
                    isTimeOverlap(meeting, slotStart, slotEnd)
            ) && meetingsUser2.stream().noneMatch(meeting ->
                    isTimeOverlap(meeting, slotStart, slotEnd)
            );

            if (slotAvailable) {
                freeSlots.add(currentStart);
            }
            currentStart = currentStart.plusMinutes(30);
        }
        return freeSlots;
    }

    private static boolean isTimeOverlap(Meeting meeting, LocalTime startTime, LocalTime endTime) {
        return !(meeting.getEndTime().isBefore(startTime) || meeting.getStartTime().isAfter(endTime));
    }
}
