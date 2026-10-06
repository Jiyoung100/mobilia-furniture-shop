document.addEventListener("DOMContentLoaded", function() {

    // FAQ 질문 클릭
    const faqQuestion = document.querySelectorAll(".faq-question");
    for(let i = 0; i < faqQuestion.length; i++) {
        faqQuestion[i].addEventListener("click", () => {
			
            const faqItem = faqQuestion[i].parentElement;
            const faqAnswer = faqItem.querySelector(".faq-answer");
            const faqArrow = faqItem.querySelector(".faq-arrow");

            if(faqAnswer.style.display === "block") {
                faqAnswer.style.display = "none";
                faqArrow.textContent = "⌄";

            } else {
                faqAnswer.style.display = "block";
                faqArrow.textContent = "⌃";
            }
        });
    }



    // FAQ 카테고리
    const faqCategory = document.querySelectorAll(".faqCategory");
    const faqItem = document.querySelectorAll(".faq-item");
	
    let category = "all";
    for(let i = 0; i < faqCategory.length; i++) {
        faqCategory[i].addEventListener("click", () => {
            category = faqCategory[i].dataset.category;

            for(let j = 0; j < faqCategory.length; j++) {
                faqCategory[j].classList.remove("active");
            }
			
			
            faqCategory[i].classList.add("active");
            searchFaq();
        });
    }


    // FAQ 검색
    const faqKeyword = document.querySelector(".faqKeyword");
    const btnFaqSearch = document.querySelector(".btnFaqSearch");
    const faqEmpty = document.querySelector(".faq-empty");

    if(btnFaqSearch != null && faqKeyword != null) {
        btnFaqSearch.addEventListener("click", () => {
            searchFaq();
        });


        faqKeyword.addEventListener("keydown", (event) => {
            if(event.key === "Enter") {
                searchFaq();
            }
        });
    }


    // FAQ 검색 함수
    function searchFaq() {
        const keyword = faqKeyword.value.toLowerCase();
        let count = 0;

        for(let i = 0; i < faqItem.length; i++) {
            const itemCategory = faqItem[i].dataset.category;
            const question = faqItem[i].querySelector(".faq-question-text").textContent.toLowerCase();
            const answer = faqItem[i].querySelector(".faq-answer").textContent.toLowerCase();
            const categoryCheck = category === "all" || category === itemCategory;
            const keywordCheck = question.includes(keyword) || answer.includes(keyword);

            if(categoryCheck && keywordCheck) {
                faqItem[i].style.display = "block";
                count++;

            } else {
                faqItem[i].style.display = "none";
            }
        }

		
        if(count === 0) {
            faqEmpty.style.display = "block";
        } else {
            faqEmpty.style.display = "none";
        }
    }

	
	
});




