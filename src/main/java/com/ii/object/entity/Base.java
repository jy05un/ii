package com.ii.object.entity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ii.object.model.enums.BroadcastStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class Base {
	
	/*
	 * BaseEntity
	 * created_at과 updated_at 필드를 남김
	 */
	@Id
	@Builder.Default
	private UUID id = UUID.randomUUID();

    @Column(updatable = false, columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @CreationTimestamp
    private OffsetDateTime createdAt;	// 생성 시각
    
    @Column(columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @UpdateTimestamp
    private OffsetDateTime updatedAt;	// 마지막 업데이트 시각
}