package com.ayush.libraryManagementSystem.repository;

import com.ayush.libraryManagementSystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository <User,Long> {
}
