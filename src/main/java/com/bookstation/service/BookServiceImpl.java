package com.bookstation.service;

import java.util.StringJoiner;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstation.dto.ManualBookRequestDTO;
import com.bookstation.entity.Book;
import com.bookstation.entity.Platform;
import com.bookstation.entity.UserBook;
import com.bookstation.repository.BookRepository;
import com.bookstation.repository.PlatformRepository;
import com.bookstation.repository.UserBookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService
{
	private final BookRepository bRepo;
	private final UserBookRepository uRepo;
	private final PlatformRepository pRepo;
	private final EmbeddingModel embeddingModel;
	
	private String toVectorString(float[] vector)
	{
		StringJoiner joiner = new StringJoiner(",", "[", "]");
		
		for(float v : vector)
		{
			joiner.add(Float.toString(v));
		}
		
		return joiner.toString();
	}
	
	@Override
	public Page<Book> bookListData(int page, String keyword, String category) 
	{
		final int ROWSIZE = 12;
		
		Pageable pg = PageRequest.of(page-1, ROWSIZE, Sort.by(Sort.Direction.ASC, "id"));
		Page<Book> bList = null;
		
		String safeKeyword = (keyword == null) ? "" : keyword.trim();
		String safeCategory = (category == null) ? "" : category;
		
		bList = bRepo.findByTitleContainingAndCategoryStartingWith(safeKeyword, safeCategory, pg);
		
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

	@Override
	@Transactional
	public Long addManualBook(ManualBookRequestDTO dto) 
	{
		Platform platform = pRepo.findById(dto.getPlatformId()).orElseThrow();
		
		Book book = new Book();
		book.setTitle(dto.getTitle());
		book.setAuthor(dto.getAuthor());
		book.setGenre(dto.getGenre());
		book.setCategory(dto.getCategory());
		book.setSynopsis(dto.getSynopsis());
		book.setCover_url(dto.getCoverUrl());
		book.setSource("MANUAL");
		
		bRepo.save(book);
		
		// 저장한 책 vector 임베딩
		if(dto.getSynopsis() != null && !dto.getSynopsis().isBlank())
		{
			try 
			{
				// embeddingModel의 embed를 사용하여 String을 부르면 vector 숫자로 변환하여 float[]로 반환
				float[] vector = embeddingModel.embed(dto.getSynopsis());	
				bRepo.updateEmbedding(book.getId(), toVectorString(vector));	// @GeneratedValue(IDENTITY)는 save()하는 순간 insert를 실행해서 DB가 만든 id를 받아오기 때문에 바로 불러올 수 있다
				
			} catch (Exception ex) 
			{
				ex.printStackTrace();	// 임베딩에 실패해도 등록은 계속
			}
		}
		
		UserBook userBook = new UserBook();
		
		userBook.setBook(book);
		userBook.setPlatform(platform);
		userBook.setStatus(dto.getStatus());
		
		return uRepo.save(userBook).getId();
	}

}
