package com.ii.object.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class Base {
	
	/*
	 * BaseEntity
	 * created_at과 updated_at 필드를 남김
	 */

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;	// 생성 시각
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;	// 마지막 업데이트 시각
}