package com.bookstation.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstation.dto.UserBookRequestDTO;
import com.bookstation.dto.UserBookResponseDTO;
import com.bookstation.entity.Book;
import com.bookstation.entity.Platform;
import com.bookstation.entity.UserBook;
import com.bookstation.repository.BookRepository;
import com.bookstation.repository.PlatformRepository;
import com.bookstation.repository.UserBookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService
{
	private final BookRepository bRepo;
	private final UserBookRepository uRepo;
	private final PlatformRepository pRepo;
	
	@Override
	@Transactional
	public Long addUserBook(UserBookRequestDTO dto) 
	{
		// 1. 책 꺼내기
		Book book = bRepo.findById(dto.getBookId()).orElseThrow();
		
		// 2. 플랫폼 꺼내기
		Platform platform = pRepo.findById(dto.getPlatformId()).orElseThrow();
		
		if(uRepo.existsByBookIdAndPlatformId(dto.getBookId(), dto.getPlatformId()))
			throw new IllegalStateException("이미 서재에 등록된 책입니다");
			
		// 3. UserBook 만들고 값 채우기
		UserBook userBook = new UserBook();
		userBook.setBook(book);
		userBook.setPlatform(platform);
		userBook.setStatus(dto.getStatus());
		userBook.setRating(dto.getRating());
		userBook.setMemo(dto.getMemo());
		userBook.setPurchased_at(dto.getPurchasedAt());
		
		// 4. 저장
		// 5. id 돌려주기
		return uRepo.save(userBook).getId();
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBookResponseDTO> userBookList() 
	{
		List<UserBook> list = uRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		List<UserBookResponseDTO> uList = new ArrayList<>();
		
		for(UserBook userBook:list)
		{
			UserBookResponseDTO dto = new UserBookResponseDTO();
			
			dto.setId(userBook.getId());
			
			dto.setBookId(userBook.getBook().getId());
			dto.setTitle(userBook.getBook().getTitle());
			dto.setAuthor(userBook.getBook().getAuthor());
			dto.setGenre(userBook.getBook().getGenre());
			dto.setCategory(userBook.getBook().getCategory());
			dto.setCoverUrl(userBook.getBook().getCover_url());
			
			dto.setPlatformId(userBook.getPlatform().getId());
			dto.setPlatformName(userBook.getPlatform().getName());
			
			dto.setMemo(userBook.getMemo());
			
			dto.setStatus(userBook.getStatus());
			dto.setRating(userBook.getRating());
			dto.setPurchasedAt(userBook.getPurchased_at());
			
			uList.add(dto);
			
		}
		
		return uList;
	}

	@Override
	@Transactional
	public void updateUserBook(UserBookRequestDTO dto, Long id) 
	{
		UserBook userBook = uRepo.findById(id).orElseThrow();
		String oldStatus = null;
		String newStatus = null;
		
		if(dto.getStatus() != null)
		{
			oldStatus = userBook.getStatus();
			userBook.setStatus(dto.getStatus());
			newStatus = userBook.getStatus();
			
			if(oldStatus.equals("WISHLIST") && !newStatus.equals("WISHLIST") && dto.getPurchasedAt()==null)
				userBook.setPurchased_at(LocalDate.now());
			if(newStatus.equals("WISHLIST") && !oldStatus.equals("WISHLIST"))
				userBook.setPurchased_at(null);
		}
		
		if(dto.getRating() != null)
		{
			if(userBook.getStatus().equals("WISHLIST") || userBook.getStatus().equals("UNREAD"))
			{
				throw new IllegalArgumentException();
			}
			if(dto.getRating() < 1 || dto.getRating() > 5)
			{
				throw new IllegalArgumentException();
			}
		
			userBook.setRating(dto.getRating());
		}
		if(dto.getMemo() != null)
			userBook.setMemo(dto.getMemo());
		if(dto.getPurchasedAt() != null)
			userBook.setPurchased_at(dto.getPurchasedAt());
		if(dto.getPlatformId() != null && !dto.getPlatformId().equals(userBook.getPlatform().getId()))
		{
			if(uRepo.existsByBookIdAndPlatformId(userBook.getBook().getId(), dto.getPlatformId())) throw new IllegalStateException();
				
			Platform platform = pRepo.findById(dto.getPlatformId()).orElseThrow();
			userBook.setPlatform(platform);
		}
	}

	@Override
	@Transactional
	public void deleteUserBook(Long id) 
	{
		if(!uRepo.existsById(id))
		{
			throw new NoSuchElementException();
		}
		
		uRepo.deleteById(id);			
	}

	@Override
	public List<Platform> platformsListData() {
		return pRepo.findAll();
		
	}

}

