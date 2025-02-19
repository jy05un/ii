package com.ii.object.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity(name = "streamer")
public class Streamer extends Base {
	
	@Column(unique = true)
	private String name;
	
	@Column(name = "profile_url")
	private String profileUrl;
	
	@Column(name = "kor_name")
	private String korName;
	
}
