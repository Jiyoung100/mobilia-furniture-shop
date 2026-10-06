package com.myapp.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.myapp.entity.Qna;
import com.myapp.repository.QnaRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QnaService {

    private final QnaRepository qnaRepository;

    // 문의 등록
    public void saveQna(Qna qna) {
        qna.setCreateDate(LocalDateTime.now());
        qnaRepository.save(qna);
    }

    // 회원별 문의 조회
    public Page<Qna> getQnaList(String customerId, int page,String status, String keyword) {
        PageRequest pageRequest = PageRequest.of(page, 10);

        // 답변대기
        if(status.equals("waiting")) {
            if(keyword.equals("")) {
                return qnaRepository.findByCustomerCustomerIdAndAnswerIsNullOrderByIdDesc(customerId, pageRequest);
            }
            return qnaRepository.findByCustomerCustomerIdAndAnswerIsNullAndTitleContainingOrderByIdDesc(customerId, keyword, pageRequest);
        }

        // 답변완료
        if(status.equals("complete")) {
            if(keyword.equals("")) {
                return qnaRepository.findByCustomerCustomerIdAndAnswerIsNotNullOrderByIdDesc(customerId, pageRequest);
            }
            return qnaRepository.findByCustomerCustomerIdAndAnswerIsNotNullAndTitleContainingOrderByIdDesc(customerId, keyword, pageRequest);
        }

        // 전체
        if(keyword.equals("")) {
            return qnaRepository.findByCustomerCustomerIdOrderByIdDesc(customerId, pageRequest);
        }
               
        return qnaRepository.findByCustomerCustomerIdAndTitleContainingOrderByIdDesc(customerId, keyword, pageRequest);
    }

    // 문의 상세 조회
    public Qna getQna(Long id) {
        return qnaRepository.findById(id).get();
   }

    // 답변 전 문의만 수정
    public void updateQna(Long id, String category, String productId, String title, String content) {
        Qna qna = qnaRepository.findById(id).get();

        if(qna.getAnswer() == null) {
            qna.setCategory(category);
            qna.setProductId(productId);
            qna.setTitle(title);
            qna.setContent(content);
            qnaRepository.save(qna);
        }
    }

    // 답변 전 문의만 삭제
    public void deleteQna(Long id) {
        Qna qna = qnaRepository.findById(id).get();

        if(qna.getAnswer() == null) {
            qnaRepository.delete(qna);
        }
    }

    // 관리자 문의 전체 조회
    public Page<Qna> getQnaListAdmin(int page, String status, String keyword) {
        PageRequest pageRequest = PageRequest.of(page, 10);

        // 답변대기
        if(status.equals("waiting")) {
            if(keyword.equals("")) {
                return qnaRepository.findByAnswerIsNullOrderByIdDesc(pageRequest);
            }
            return qnaRepository.findByAnswerIsNullAndTitleContainingOrderByIdDesc(keyword, pageRequest);
        }

        // 답변완료
        if(status.equals("complete")) {
            if(keyword.equals("")) {
                return qnaRepository.findByAnswerIsNotNullOrderByIdDesc(pageRequest);
            }
            return qnaRepository.findByAnswerIsNotNullAndTitleContainingOrderByIdDesc(keyword, pageRequest);
        }

        // 전체
        if(keyword.equals("")) {
            return qnaRepository.findAllByOrderByIdDesc(pageRequest);
        }
        return qnaRepository.findByTitleContainingOrderByIdDesc(keyword, pageRequest);
    }

    // 관리자 답변 등록
    public void saveAnswer(Long id, String answer) {
        Qna qna = qnaRepository.findById(id).get();

        qna.setAnswer(answer);
        qna.setAnswerDate(LocalDateTime.now());
        qnaRepository.save(qna);
    }    

    // 관리자 문의 삭제
    public void deleteQnaAdmin(Long id) {

        Qna qna = qnaRepository.findById(id).get();
        qnaRepository.delete(qna);
    }

}