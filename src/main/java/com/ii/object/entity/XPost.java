package com.ii.object.entity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.JsonJdbcType;

import com.ii.object.model.enums.BroadcastStatus;
import com.ii.object.model.enums.ChatType;
import com.ii.object.model.enums.RefType;

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
import lombok.experimental.SuperBuilder;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity(name = "x_post")
public class XPost extends Base {

	@Column(name = "post_id", unique = true)
	private String postId;

	@Column(columnDefinition = "TEXT")
	private String url;
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	@Column(name = "uploaded_at", columnDefinition= "TIMESTAMP WITH TIME ZONE")
	private OffsetDateTime uploadedAt;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "ref_type")
	private RefType refType;
	
	@JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ref_profile_json", columnDefinition = "jsonb")
    private Map<String, Object> refProfileJson;
	
	@ManyToOne
	@JoinColumn(name = "streamer_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Streamer streamer;
	
}
