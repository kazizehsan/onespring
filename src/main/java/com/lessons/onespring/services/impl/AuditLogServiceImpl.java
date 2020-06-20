package com.lessons.onespring.services.impl;


import com.lessons.onespring.dto.AuditLogDto;
import com.lessons.onespring.entities.AuditLog;
import com.lessons.onespring.entities.User;
import com.lessons.onespring.exceptions.EntityNotFoundException;
import com.lessons.onespring.repositories.AuditLogRepository;
import com.lessons.onespring.repositories.UserRepository;
import com.lessons.onespring.services.intf.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private AuditLogRepository auditLogRepository;
    private UserRepository userRepository;

    public AuditLogServiceImpl(
            AuditLogRepository auditLogRepository,
            UserRepository userRepository
    ) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    private AuditLog constructAuditLog(
            Boolean success,
            String origin,
            String action,
            String extraInfo
    ) {
        AuditLog auditLog = new AuditLog();

        auditLog.setSuccess(success);
        auditLog.setOrigin(origin);
        auditLog.setAction(action);
        auditLog.setExtraInfo(extraInfo);

        return auditLog;
    }

    @Override
    public AuditLog save(
            User createdBy,
            Boolean success,
            String origin,
            String action,
            String extraInfo
    ) {
        AuditLog auditLog = constructAuditLog(success, origin, action, extraInfo);
        auditLog.setCreatedBy(createdBy.getEmail());

        auditLogRepository.save(auditLog);

        return auditLog;
    }

    @Override
    public AuditLog save(
            Boolean success,
            String origin,
            String action,
            String extraInfo
    ) {
        AuditLog auditLog = constructAuditLog(success, origin, action, extraInfo);

        auditLogRepository.save(auditLog);

        return auditLog;
    }

    @Override
    public Page<AuditLogDto> findAll(Pageable pageable) {
        return auditLogRepository.findAll(pageable).map(AuditLogDto::entityToDto);
    }

    @Override
    public AuditLog findById(Long id) {
        return auditLogRepository.
                findById(id).
                orElseThrow(() -> new EntityNotFoundException("Audit Log", "id", id));
    }

    @Override
    public boolean exists(Long id) {
        return auditLogRepository.existsById(id);
    }

    @Override
    public Page<AuditLogDto> findAllByUserAndCreatedDateAndAction(Long createdBy, Date fromDate, Date toDate, String action, Pageable pageable) {
        String email = "";
        if (createdBy != 0L) {
            User user = userRepository.findById(createdBy).orElseThrow(() -> new EntityNotFoundException("user", "id", createdBy));
            email = user.getEmail();
        }
        return auditLogRepository.findAllByCreatedByContainsAndCreatedDateBetweenAndActionContains(email, fromDate, toDate, action, pageable).map(AuditLogDto::entityToDto);
    }
}
