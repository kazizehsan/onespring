package com.lessons.onespring.repositories;

import com.lessons.onespring.entities.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findAllByCreatedDateBetween(Date fromDate, Date toDate, Pageable pageable);

    Page<AuditLog> findAllByCreatedByAndCreatedDateBetween(String createdBy, Date fromDate, Date toDate, Pageable pageable);

    Page<AuditLog> findAllByCreatedBy(String createdBy, Pageable pageable);

    Page<AuditLog> findAllByAction(String action, Pageable pageable);

    Page<AuditLog> findAllByCreatedByAndCreatedDateBetweenAndAction(String createdBy, Date fromDate, Date toDate, String action, Pageable pageable);

    Page<AuditLog> findAllByCreatedByContainsAndCreatedDateBetweenAndActionContains(String email, Date fromDate, Date toDate, String action, Pageable pageable);
}
