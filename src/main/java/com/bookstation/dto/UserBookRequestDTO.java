package com.bookstation.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserBookRequestDTO {
	private Long bookId;
	private Long platformId;
	private String status;
	private Integer rating;
	private String memo;
	private LocalDate purchasedAt;
}
