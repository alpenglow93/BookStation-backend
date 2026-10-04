package com.bookstation.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstation.dto.RecommendReasonDTO;
import com.bookstation.dto.RecommendResponseDTO;
import com.bookstation.entity.Book;
import com.bookstation.entity.UserBook;
import com.bookstation.repository.BookRepository;
import com.bookstation.repository.UserBookRepository;

@Service
public class RecommendServiceImpl implements RecommendService 
{
	private final BookRepository bRepo;
	private final UserBookRepository uRepo;
	private final ChatClient chatClient;
	
	public RecommendServiceImpl(BookRepository bRepo, UserBookRepository uRepo, ChatClient.Builder builder) {
		
		this.bRepo = bRepo;
		this.uRepo = uRepo;
		this.chatClient = builder.build();
	}
	
	private String normalizeTitle(String title)
	{
		return title.replaceAll("\\s*\\d+(권|화)$", "").trim();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Book> recommendBook(String category, int limit) 
	{
		return bRepo.findRecommendBook(category, limit);
	}

	@Override
	public List<RecommendResponseDTO> recommendWithReason(String category, int limit) 
	{
		// 1. 추천 책 10권 가져오기
		List<Book> recommendList = bRepo.findRecommendBook(category, limit);
		
		if(recommendList.isEmpty())
			return new ArrayList<>();
		
		// 2. 내가 좋아한 책 제목 가져오기
		List<UserBook> userBookList = uRepo.findByStatusInAndRatingGreaterThanEqual(List.of("UNREAD", "READING", "COMPLETED"), 4);
		
		// 3. 1, 2를 글로 엮어서 질문 만들기
		StringBuilder sb = new StringBuilder();
		sb.append("너는 웹소설 추천 도우미야.\n");
		sb.append("사용자가 좋아한 작품: ");
		for(UserBook ub : userBookList)
		{
			if(category == null || ub.getBook().getCategory().startsWith(category))
			{
				sb.append(ub.getBook().getTitle()).append(", ");
			}
		}
		sb.append("\n\n");
		
		sb.append("아래 후보 도서 각각에 대해 사용자의 취향과 연결해서 왜 추천하는지 한국어 1~2 문장으로 써줘. \n\n");
		
		for(Book b : recommendList)
		{
			String synopsis = b.getSynopsis()==null ? "" : b.getSynopsis() ;
			
			sb.append("- id: ").append(b.getId())
				.append(" / 제목: ").append(b.getTitle())
				.append(" / 장르: ").append(b.getGenre())
				.append(" / 소개: ").append(synopsis)
				.append("\n");
				
		}
		
		String prompt = sb.toString();
		System.out.println(prompt);
		
		
		// 4. Gemini에 질문하고 답 받기
		List<RecommendReasonDTO> reasons = null;
		
		try {
			reasons = chatClient.prompt()
					.user(prompt)
					.call()
					.entity(new ParameterizedTypeReference<List<RecommendReasonDTO>>() {
					});
			
			System.out.println(reasons);
			
		} catch (Exception ex) 
		{
			ex.printStackTrace();
			reasons = new ArrayList<>();
		}
		
		if(reasons == null) reasons = new ArrayList<>();
		
		// 5. 책 + 이유 합쳐서 돌려주기
		Map<Long, String> map = new HashMap<>();
		
		for(RecommendReasonDTO r : reasons)
		{
			map.put(r.bookId(), r.reason());
		}
		
		List<RecommendResponseDTO> result = new ArrayList<>();
		
		for(Book b : recommendList)
		{
			RecommendResponseDTO dto = new RecommendResponseDTO();
			// 책 정보 5개 setter로 채우기
			dto.setBookId(b.getId());
			dto.setTitle(b.getTitle());
			dto.setAuthor(b.getAuthor());
			dto.setCategory(b.getCategory());
			dto.setCoverUrl(b.getCover_url());
			
			// 이유 : map.getOrDefault(b.getId(), "")
			dto.setReason(map.getOrDefault(b.getId(), ""));
			
			result.add(dto);
		}
		
		return result;
	}
	
	
}
