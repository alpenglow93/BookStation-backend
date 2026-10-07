package com.bookstation.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserBookResponseDTO {
	private Long id;
	
	private Long bookId;
	private String title;
	private String author;
	private String genre;
	private String category;
	private String coverUrl;
	
	private Long platformId;
	private String platformName;

	private String memo;
	
	private String status;
	private Integer rating;
	private LocalDate purchasedAt;
}
