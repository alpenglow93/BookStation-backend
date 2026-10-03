package com.bookstation.service;

import java.util.List;

import com.bookstation.dto.RecommendResponseDTO;
import com.bookstation.entity.Book;

public interface RecommendService 
{
	List<Book> recommendBook(String category, int limit);
	
	public List<RecommendResponseDTO> recommendWithReason(String category, int limit);
}
