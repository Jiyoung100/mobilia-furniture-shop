document.addEventListener("DOMContentLoaded", function() {

    const btnFaq = document.querySelector(".btnFaq");
    const btnQna = document.querySelector(".btnQna");
    const faqSection = document.querySelector(".faq-section");
    const qnaSection = document.querySelector(".qna-section");

	if(btnFaq != null && btnQna != null) {

	    // FAQ
	    btnFaq.addEventListener("click", () => {
	        // 관리자
	        if(btnFaq.dataset.admin === "true") {
	            location.href = "/support";
	            return;
	        }
	        location.href = "/support";
	    });

	    // Q&A
	    btnQna.addEventListener("click", () => {

	        // 관리자
	        if(btnQna.dataset.admin === "true") {
	            location.href = "/support/admin/list";
	            return;
	        }

	        // 비회원
	        if(btnQna.dataset.login !== "true") {
	            alert("로그인 후 이용해주세요.");
	            location.href = "/login";
	            return;
	        }

	        // 일반 회원
	        location.href = "/support?qna=true";
	    });

	    // Q&A 화면
	    if(location.search.includes("qna=true")) {
	        faqSection.style.display = "none";
	        qnaSection.style.display = "block";
	        btnFaq.classList.remove("active");
	        btnQna.classList.add("active");
	    }
	}

});