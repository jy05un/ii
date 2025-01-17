package com.ii.object.entity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import com.ii.object.model.enums.BroadcastStatus;
import com.ii.object.model.enums.ChatType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity(name = "ig_post")
public class IgPost extends Base {

	@Column(name = "post_id", unique = true)
	private String postId;

	@Column(columnDefinition = "TEXT")
	private String url;
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	@Column(name = "uploaded_at", columnDefinition= "TIMESTAMP WITH TIME ZONE")
	private OffsetDateTime uploadedAt;
	
	@ManyToOne
	@JoinColumn(name = "streamer_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Streamer streamer;
	
}
