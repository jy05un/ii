package com.ii.object.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import com.ii.object.model.enums.BroadcastStatus;
import com.ii.object.model.enums.ChatType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Entity(name = "chat_record")
public class ChatRecord extends Base {

	@Id
	@Builder.Default
	private UUID id = UUID.randomUUID();

	@Column(columnDefinition = "TEXT")
	private String url;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "chat_type")
	private ChatType chatType;
	
	@Column(name = "sent_at")
	private LocalDateTime sentAt;
	
	@ManyToOne
	@JoinColumn(name = "streamer_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Streamer streamer;
	
}
