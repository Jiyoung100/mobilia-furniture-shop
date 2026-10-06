package com.myapp.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;

import com.myapp.entity.Customer;
import com.myapp.entity.Qna;
import com.myapp.entity.Role;
import com.myapp.service.CustomerService;
import com.myapp.service.QnaService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class SupportController {

	private final QnaService qnaService;
	private final CustomerService customerService;

    // 고객센터
	@GetMapping("/support")
	public String support(
	        @RequestParam(value = "page", defaultValue = "0") int page,
	        @RequestParam(value = "status", defaultValue = "all") String status,
	        @RequestParam(value = "keyword", defaultValue = "") String keyword,
	        Model model, Principal principal) {

	    model.addAttribute("adminQna", false);
	    model.addAttribute("login", principal != null);
	    model.addAttribute("status", status);
	    model.addAttribute("keyword", keyword);

	    if(principal != null) {
	        Customer customer = customerService.selectCustomer(principal.getName());

	        if(customer.getRole() == Role.ADMIN ||
	           customer.getRole() == Role.MANAGER) {
	            model.addAttribute("adminQna", true);

	        } else {
	            Page<Qna> qnaPage = qnaService.getQnaList(principal.getName(), page, status, keyword);
	            model.addAttribute("qnaList", qnaPage.getContent());
	            model.addAttribute("qnaPage", qnaPage);
	        }
	    }
	    return "support/question";
	}

    // Q&A 작성 페이지
    @GetMapping("/support/qna/write")
    public String qnaWrite() {
        return "support/qnaWrite";
    }
    
    // Q&A 등록
    @PostMapping("/support/qna/write")
    public String qnaWrite(Qna qna, Principal principal) {

        Customer customer = customerService.selectCustomer(principal.getName());

        qna.setCustomer(customer);
        qnaService.saveQna(qna);
        
        return "redirect:/support?qna=true";
    }    
    
    // Q&A 상세 조회
    @GetMapping("/support/qna/detail/{id}")
    public String qnaDetail(@PathVariable("id") Long id, Model model, Principal principal) {
        Qna qna = qnaService.getQna(id);

        if(!qna.getCustomer().getCustomerId().equals(principal.getName())) {
            return "redirect:/support";
        }

        model.addAttribute("qna", qna);
        return "support/qnaDetail";
    }

    // Q&A 수정 페이지
    @GetMapping("/support/qna/modify/{id}")
    public String qnaModify(@PathVariable("id") Long id, Model model, Principal principal) {
        Qna qna = qnaService.getQna(id);

        if(!qna.getCustomer().getCustomerId().equals(principal.getName())) {
            return "redirect:/support";
        }
        if(qna.getAnswer() != null) {
            return "redirect:/support/qna/detail/" + id;
        }

        model.addAttribute("qna", qna);
        return "support/qnaModify";
    }    

    // Q&A 수정
    @PostMapping("/support/qna/modify/{id}")
    public String qnaModify(@PathVariable("id") Long id,
                            @RequestParam("category") String category,
                            @RequestParam("productId") String productId,
                            @RequestParam("title") String title,
                            @RequestParam("content") String content,
                            Principal principal) {

        Qna qna = qnaService.getQna(id);

        if(!qna.getCustomer().getCustomerId().equals(principal.getName())) {
            return "redirect:/support";
        }
        qnaService.updateQna(id, category, productId, title, content);	
        return "redirect:/support/qna/detail/" + id;
    }

    // Q&A 삭제
    @PostMapping("/support/qna/delete/{id}")
    public String qnaDelete(@PathVariable("id") Long id, Principal principal) {

        Qna qna = qnaService.getQna(id);
        if(!qna.getCustomer().getCustomerId().equals(principal.getName())) {
            return "redirect:/support";
        }
        qnaService.deleteQna(id);
        return "redirect:/support?qna=true";
    }    

    // 관리자 Q&A 전체 조회
    @GetMapping("/support/admin/list")
    public String qnaAdminList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "status", defaultValue = "all") String status,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            Model model, Principal principal) {

        if(principal == null) {
            return "redirect:/login";
        }

        Customer customer = customerService.selectCustomer(principal.getName());
        
        if(customer.getRole() != Role.ADMIN && customer.getRole() != Role.MANAGER) {
            return "redirect:/support";
        }
        Page<Qna> qnaPage = qnaService.getQnaListAdmin(page, status, keyword);

        model.addAttribute("qnaList", qnaPage.getContent());
        model.addAttribute("qnaPage", qnaPage);
        model.addAttribute("status", status);
        model.addAttribute("keyword", keyword);

        return "support/qnaAdminList";
    }

    // 관리자 Q&A 상세 조회
    @GetMapping("/support/admin/detail/{id}")
    public String qnaAdminDetail(@PathVariable("id") Long id, Model model, Principal principal) {

        if(principal == null) {
            return "redirect:/login";
        }
        Customer customer = customerService.selectCustomer(principal.getName());

        if(customer.getRole() != Role.ADMIN && customer.getRole() != Role.MANAGER) {
            return "redirect:/support";
        }
        Qna qna = qnaService.getQna(id);

        model.addAttribute("qna", qna);
        return "support/qnaAdminDetail";
    }

    // 관리자 Q&A 답변 등록
    @PostMapping("/support/admin/answer/{id}")
    public String qnaAdminAnswer(@PathVariable("id") Long id, @RequestParam("answer") String answer, Principal principal) {

        if(principal == null) {
            return "redirect:/login";
        }
        Customer customer = customerService.selectCustomer(principal.getName());

        if(customer.getRole() != Role.ADMIN && customer.getRole() != Role.MANAGER) {
            return "redirect:/support";
        }

        qnaService.saveAnswer(id, answer);
        return "redirect:/support/admin/detail/" + id;
    }

    // 관리자 Q&A 삭제
    @PostMapping("/support/admin/delete/{id}")
    public String qnaAdminDelete(@PathVariable("id") Long id, Principal principal) {
        if(principal == null) {
            return "redirect:/login";
        }
        Customer customer = customerService.selectCustomer(principal.getName());

        if(customer.getRole() != Role.ADMIN && customer.getRole() != Role.MANAGER) {
            return "redirect:/support";
        }

        qnaService.deleteQnaAdmin(id);
        return "redirect:/support/admin/list";
    }    
    
}