package com.bookstation.dto;

import lombok.Data;

@Data
public class RecommendResponseDTO {
	private Long bookId;
	private String title;
	private String author;
	private String category;
	private String coverUrl;
	private String reason;

}
