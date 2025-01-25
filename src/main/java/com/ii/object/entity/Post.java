package com.ii.object.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.ii.object.model.enums.PostType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
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
	
	@OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
	private List<BookmarkPost> bookmarks = new ArrayList<BookmarkPost>();
	
	public Object getPostObject() {
		Object data = switch (this.getType()) {
			case PostType.Cafe		-> this.getCafePost();
			case PostType.Soop		-> this.getSoopPost();
			case PostType.X 		-> this.getXPost();
			case PostType.Instagram	-> this.getIgPost();
			default 				-> throw new IllegalArgumentException("Unexpected value: " + this.getType());
		};
		return data;
	}
	
}
