package com.ii.object.model.DTO;

import java.util.ArrayList;
import java.util.List;

import com.ii.object.entity.BookmarkSummary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetBookmarksResDTO {
	
	List<BookmarkSummary> bookmarks = new ArrayList<BookmarkSummary>();
	int size;

}
