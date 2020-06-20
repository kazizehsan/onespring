package com.lessons.onespring.services.intf;

import com.lessons.onespring.entities.User;
import com.lessons.onespring.dto.AuditLogDto;
import com.lessons.onespring.entities.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

public interface AuditLogService {

    AuditLog save(User createdBy, Boolean success, String origin, String action, String extraInfo);

    AuditLog save(Boolean success, String origin, String action, String extraInfo);

    Page<AuditLogDto> findAll(Pageable pageable);

    AuditLog findById(Long id);

    boolean exists(Long id);

    Page<AuditLogDto> findAllByUserAndCreatedDateAndAction(Long createdBy, Date fromDate, Date toDate, String action, Pageable pageable);
}
