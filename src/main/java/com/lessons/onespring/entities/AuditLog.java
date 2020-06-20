package com.lessons.onespring.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "audit_logs")
@EntityListeners(AuditingEntityListener.class)
public class AuditLog {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    @Nullable
    private Date createdDate;

    @CreatedBy
    @Column(name = "created_by")
    @Nullable
    private String createdBy;

    @Column(name = "origin")
    private String origin;

    @Column(name = "action")
    private String action;

    @Column(name = "extra_info")
    private String extraInfo;

    @Column(name = "success")
    private Boolean success;
}
