package com.lessons.onespring.entities;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditEntity<U> {

    @CreatedBy
    @Column(name = "created_by")
    @Nullable
    private U createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    @Nullable
    private U lastModifiedBy;


    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    @Nullable
    private Date createdDate;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    @Nullable
    private Date lastModifiedDate;

    public AuditEntity() {
    }

    @Nullable
    public U getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(@Nullable U createdBy) {
        this.createdBy = createdBy;
    }

    @Nullable
    public U getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(@Nullable U lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Nullable
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nullable Date createdDate) {
        this.createdDate = createdDate;
    }

    @Nullable
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(@Nullable Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
