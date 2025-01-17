package com.ii.object.entity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import com.ii.object.model.enums.PostType;
import com.ii.object.model.enums.RefType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Entity(name = "post")
public class Post extends Base {
	
	@Enumerated(EnumType.STRING)
	private PostType type;
	
	@ManyToOne
	@JoinColumn(name = "streamer_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Streamer streamer;
	
	@Column(name = "uploaded_at", columnDefinition= "TIMESTAMP WITH TIME ZONE")
	private OffsetDateTime uploadedAt;
	
	@OneToOne
	@JoinColumn(name = "cafe_post_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private CafePost cafePost;
	
	@OneToOne
	@JoinColumn(name = "x_post_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private XPost xPost;
	
	@OneToOne
	@JoinColumn(name = "ig_post_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private IgPost igPost;
	
	@OneToOne
	@JoinColumn(name = "soop_post_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private SoopPost soopPost;
	
	@OneToOne
	@JoinColumn(name = "yt_post_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private YtPost ytPost;
	
}
