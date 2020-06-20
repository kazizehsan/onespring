package com.lessons.onespring.controllers;


import com.lessons.onespring.exceptions.BadRequestException;
import com.lessons.onespring.exceptions.EntityNotFoundException;
import com.lessons.onespring.services.intf.AuditLogService;
import com.lessons.onespring.aop.AspectEventType;
import com.lessons.onespring.dto.AuditLogDto;;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.lessons.onespring.constants.Constant.PRIVILEGE_ADMINISTRATOR;

@RestController
@RequestMapping("/audits")
public class AuditLogController {

    private AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }


    private Date parseDateFromRequestParam(String date) {
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            throw new BadRequestException("invalid date format, required format: dd-MM-yyyy");
        }
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('" + PRIVILEGE_ADMINISTRATOR + "')")
    public Page<AuditLogDto> findAll(
            @RequestParam(value = "fromDate", required = false, defaultValue = "") String fromDate,
            @RequestParam(value = "toDate", required = false, defaultValue = "") String toDate,
            @RequestParam(value = "user", required = false, defaultValue = "0") Long userID,
            @RequestParam(value = "action", required = false, defaultValue = "") String action,
            Pageable pageable
    ) {
        Date from;
        Date to;
        if (toDate.isBlank()) {
            to = new Date();
        } else {
            to = parseDateFromRequestParam(toDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(to);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            to = cal.getTime();
        }
        if (fromDate.isBlank()) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -10);
            from = cal.getTime();
        } else {
            from = parseDateFromRequestParam(fromDate);
        }
        return auditLogService.findAllByUserAndCreatedDateAndAction(userID, from, to, action, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + PRIVILEGE_ADMINISTRATOR + "')")
    public AuditLogDto findById(@PathVariable Long id) {
        if (!auditLogService.exists(id)) {
            throw new EntityNotFoundException("audit log not found");
        }
        return AuditLogDto.entityToDto(auditLogService.findById(id));
    }

    @GetMapping("/types")
    @PreAuthorize("hasAuthority('" + PRIVILEGE_ADMINISTRATOR + "')")
    public ResponseEntity<?> getEventTypes() {
        Map<Object, Object> model = new HashMap<>();
        model.put("event_types", AspectEventType.values());

        return ResponseEntity.ok(model);
    }
}
