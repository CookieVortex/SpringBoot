package com.example.springboot;

import com.example.springboot.entity.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserLogRepository extends JpaRepository<UserLog, Long> {

}

