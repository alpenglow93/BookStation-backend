package com.bookstation.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.bookstation.entity.Book;
import com.bookstation.repository.BookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService
{
	private final BookRepository bRepo;
	
	@Override
	public Page<Book> bookListData(int page, String keyword) 
	{
		final int ROWSIZE = 12;
		
		Pageable pg = PageRequest.of(page-1, ROWSIZE, Sort.by(Sort.Direction.ASC, "id"));
		Page<Book> bList = null;
		
		if(keyword == null || keyword.isBlank())
		{
			 bList = bRepo.findAll(pg);			
		}
		else
		{
			bList = bRepo.findByTitleContaining(keyword, pg);
		}
		
		return bList;
	}

	@Override
	public int[] getPageData(int page, int total) 
	{
		int[] datas = new int[4];
		
		int totalpage = total;
		final int BLOCK=10;
		int startPage = ((page-1)/BLOCK*BLOCK)+1;
		int endPage = ((page-1)/BLOCK*BLOCK)+BLOCK;
		if(endPage>totalpage)
			endPage = totalpage;
		datas[0] = page;
		datas[1] = totalpage;
		datas[2] = startPage;
		datas[3] = endPage;
		
		return datas;
	}

	@Override
	public Book bookDetailData(Long id) 
	{	
		return bRepo.findById(id).orElse(null);
	}

}
