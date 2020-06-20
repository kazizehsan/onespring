package com.lessons.onespring.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.lessons.onespring.entities.AuditLog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class AuditLogDto {
    private Long id;
    private String origin;
    private String action;
    private String extraInfo;
    private boolean success;

    private Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")

    private @Nullable
    String createdBy;

    public static AuditLogDto entityToDto(AuditLog auditLog){
        return new AuditLogDto()
                .setId(auditLog.getId())
                .setOrigin(auditLog.getOrigin())
                .setAction(auditLog.getAction())
                .setExtraInfo(auditLog.getExtraInfo())
                .setCreatedAt(auditLog.getCreatedDate())
                .setSuccess(auditLog.getSuccess())
                .setCreatedBy(auditLog.getCreatedBy());
    }
}
