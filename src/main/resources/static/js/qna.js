document.addEventListener("DOMContentLoaded", function() {

    // 문의 작성 취소
    const btnQnaCancel = document.querySelector(".btnQnaCancel");
	
    if(btnQnaCancel != null) {
        btnQnaCancel.addEventListener("click", () => {

            if(confirm("문의 작성을 취소하시겠습니까?")) {
                history.back();
            }
        });
    }

	// Q&A 등록 확인
	const qnaWriteForm = document.querySelector(".qnaWriteForm");
	if(qnaWriteForm != null) {
	    qnaWriteForm.addEventListener("submit", (e) => {

	        const category = qnaWriteForm.querySelector("[name='category']");
	        const title = qnaWriteForm.querySelector("[name='title']");
	        const content = qnaWriteForm.querySelector("[name='content']");

	        // 문의분류 확인
	        if(category.value === "") {
	            alert("문의분류를 선택해주세요.");
	            e.preventDefault();
	            return;
	        }
	        // 제목 확인
	        if(title.value.trim() === "") {
	            alert("제목을 입력해주세요.");
	            e.preventDefault();
	            return;
	        }
	        // 문의내용 확인
	        if(content.value.trim() === "") {
	            alert("문의내용을 입력해주세요.");
	            e.preventDefault();
	            return;
	        }
	        // 문의 등록 확인
	        if(!confirm("문의를 등록하시겠습니까?")) {
	            e.preventDefault();
	        }
	    });
	}

    // Q&A 삭제 확인
    const qnaDeleteForm = document.querySelector(".qnaDeleteForm");
	
    if(qnaDeleteForm != null) {
        qnaDeleteForm.addEventListener("submit", (e) => {

            if(!confirm("문의를 삭제하시겠습니까?")) {
                e.preventDefault();
            }
        });
    }

    // 관리자 답변 등록
    const qnaAnswerForm = document.querySelector(".qna-answer-form");

    if(qnaAnswerForm != null) {
        qnaAnswerForm.addEventListener("submit", (e) => {
            const answer = qnaAnswerForm.querySelector("textarea");

            // 답변 내용 확인
            if(answer.value.trim() === "") {
                alert("답변 내용을 입력해주세요.");
                e.preventDefault();
                return;
            }
            // 답변 등록 확인
            if(!confirm("답변을 등록하시겠습니까?")) {
                e.preventDefault();
            }
        });
    }

    // 관리자 문의 삭제 확인
    const qnaAdminDeleteForm = document.querySelector(".qnaAdminDeleteForm");

    if(qnaAdminDeleteForm != null) {
        qnaAdminDeleteForm.addEventListener("submit", (e) => {

            if(!confirm("문의를 삭제하시겠습니까?")) {
                e.preventDefault();
            }
        });
    }

});



