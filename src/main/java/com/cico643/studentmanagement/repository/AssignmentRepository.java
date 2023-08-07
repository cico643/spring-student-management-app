package com.cico643.studentmanagement.repository;

import com.cico643.studentmanagement.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
}
