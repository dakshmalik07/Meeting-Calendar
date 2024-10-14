package com.assigment1.meetingcalendar.repository;

import com.assigment1.meetingcalendar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
