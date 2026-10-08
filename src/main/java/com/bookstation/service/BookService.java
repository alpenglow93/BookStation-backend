package com.bookstation.service;

import java.util.*;

import org.springframework.data.domain.Page;

import com.bookstation.dto.ManualBookRequestDTO;
import com.bookstation.entity.Book;

public interface BookService 
{
	public Page<Book> bookListData(int page, String keyword, String category);
	public int[] getPageData(int page, int total);
	public Book bookDetailData(Long id);
	
	public Long addManualBook(ManualBookRequestDTO dto);
}
