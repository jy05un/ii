package com.ii.object.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import com.ii.object.model.enums.BroadcastStatus;
import com.ii.object.model.enums.ChatType;
import com.ii.object.model.enums.FileType;

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
@Entity(name = "file")
public class File extends Base {

	@Id
	@Builder.Default
	private UUID id = UUID.randomUUID();
	
	@Column(columnDefinition = "TEXT")
	private String name;
	
	@Column(name = "mime_type", columnDefinition = "TEXT")
	private String mimeType;
	
	@Column(columnDefinition = "TEXT")
	private Integer size;

	@Column(columnDefinition = "TEXT")
	private String url;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "file_type")
	private FileType fileType;
	
	@ManyToOne
	@JoinColumn(name = "post_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Post post;
	
}
