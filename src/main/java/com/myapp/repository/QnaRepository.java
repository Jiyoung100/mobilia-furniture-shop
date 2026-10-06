package com.myapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.myapp.entity.Qna;

public interface QnaRepository extends JpaRepository<Qna, Long> {

	// 회원별 문의 조회
	Page<Qna> findByCustomerCustomerIdOrderByIdDesc(String customerId, Pageable pageable);

	// 회원별 제목 검색
	Page<Qna> findByCustomerCustomerIdAndTitleContainingOrderByIdDesc(String customerId, String keyword, Pageable pageable);

	// 회원별 답변대기
	Page<Qna> findByCustomerCustomerIdAndAnswerIsNullOrderByIdDesc(String customerId, Pageable pageable);

	// 회원별 답변대기 + 제목 검색
	Page<Qna> findByCustomerCustomerIdAndAnswerIsNullAndTitleContainingOrderByIdDesc(String customerId, String keyword, Pageable pageable);

	// 회원별 답변완료
	Page<Qna> findByCustomerCustomerIdAndAnswerIsNotNullOrderByIdDesc(String customerId, Pageable pageable);

	// 회원별 답변완료 + 제목 검색
	Page<Qna> findByCustomerCustomerIdAndAnswerIsNotNullAndTitleContainingOrderByIdDesc(String customerId, String keyword, Pageable pageable);

	// 관리자 문의 전체 조회
	Page<Qna> findAllByOrderByIdDesc(Pageable pageable);

	// 관리자 제목 검색
	Page<Qna> findByTitleContainingOrderByIdDesc(String keyword, Pageable pageable);

	// 관리자 답변대기
	Page<Qna> findByAnswerIsNullOrderByIdDesc(Pageable pageable);

	// 관리자 답변대기 + 제목 검색
	Page<Qna> findByAnswerIsNullAndTitleContainingOrderByIdDesc(String keyword, Pageable pageable);

	// 관리자 답변완료
	Page<Qna> findByAnswerIsNotNullOrderByIdDesc(Pageable pageable);

	// 관리자 답변완료 + 제목 검색
	Page<Qna> findByAnswerIsNotNullAndTitleContainingOrderByIdDesc(String keyword, Pageable pageable);

}