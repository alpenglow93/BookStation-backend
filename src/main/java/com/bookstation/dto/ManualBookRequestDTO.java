package com.bookstation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ManualBookRequestDTO {
	@NotBlank
	private String title;
	
	@NotNull
	private Long platformId;
	
	private String author, genre, category, synopsis, coverUrl, status;
}
