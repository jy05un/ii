package com.ii.object.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.ii.object.model.enums.BroadcastStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "cafe_post")
public class CafePost extends Base {

	@Id
	@Builder.Default
	private UUID id = UUID.randomUUID();
	
	@Column(name = "post_id", unique = true)
	private String postId;

	@Column(columnDefinition = "TEXT")
	private String category;
	
	@Column(columnDefinition = "TEXT")
	private String title;
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	@Column(name = "uploaded_at", columnDefinition= "TIMESTAMP WITH TIME ZONE")
	private LocalDateTime uploadedAt;
	
	@ManyToOne
	@JoinColumn(name = "streamer_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Streamer streamer;
	
}
