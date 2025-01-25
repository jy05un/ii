package com.ii.object.entity;

import java.util.UUID;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(
		name = "bookmark_post",
		uniqueConstraints = {
		        @UniqueConstraint(columnNames = {"bookmark_id", "post_id"})
		    }
		)
public class BookmarkPost extends Base {

	@ManyToOne
	@JoinColumn(name = "bookmark_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Bookmark bookmark;
	
	@ManyToOne
	@JoinColumn(name = "post_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Post post;
	
}
