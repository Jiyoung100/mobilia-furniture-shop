document.addEventListener("DOMContentLoaded", function () {
    // 1. 주문 상세 접기 / 펼치기 제어
    document.querySelectorAll(".order-collapse").forEach(function (orderCollapse) {
        const orderId = orderCollapse.dataset.orderId;
        const extraText = document.getElementById("order-extra-" + orderId);
        const arrow = document.getElementById("order-arrow-" + orderId);

        orderCollapse.addEventListener("show.bs.collapse", function () {
            if (extraText) {
                extraText.classList.add("d-none");
            }
            if (arrow) {
                arrow.classList.remove("bi-chevron-down");
                arrow.classList.add("bi-chevron-up");
            }
        });

        orderCollapse.addEventListener("hide.bs.collapse", function () {
            if (extraText) {
                extraText.classList.remove("d-none");
            }
            if (arrow) {
                arrow.classList.remove("bi-chevron-up");
                arrow.classList.add("bi-chevron-down");
            }
        });
    });

    // 2. 후기 작성 / 수정 모달 제어
    const reviewModalEl = document.getElementById("reviewModal");
    if (!reviewModalEl) return;
    const reviewModal = new bootstrap.Modal(reviewModalEl);

    const productSelect = document.getElementById("modalProductSelect");
    const reviewForm = document.getElementById("reviewForm");
    const modalTitle = document.getElementById("modalTitle");
    const btnSubmit = document.getElementById("btnSubmitReview");
    const modalOrderItemId = document.getElementById("modalOrderItemId");
    const modalReviewId = document.getElementById("modalReviewId");
    const modalRating = document.getElementById("modalRating");
    const modalContent = document.getElementById("modalContent");
    const currentImageNotice = document.getElementById("currentImageNotice");
    const reviewImageInput = document.getElementById("reviewImage");

    // '후기 작성/수정' 버튼 클릭 시
    document.querySelectorAll(".btnOpenReviewModal").forEach(function (button) {
        button.addEventListener("click", function () {
            const orderId = this.id.replace("btn-order-", "");
            const itemsContainer = document.getElementById("order-items-data-" + orderId);
            if (!itemsContainer) return;

            const items = itemsContainer.querySelectorAll(".order-item-meta");

            // 셀렉트 박스 옵션 초기화 후 재구성
            productSelect.innerHTML = "";
            items.forEach(function (item) {
                const opt = document.createElement("option");
                opt.value = item.dataset.id;
                opt.textContent = item.dataset.name;
                productSelect.appendChild(opt);
            });

            // 첫 번째 옵션 상품 기준 리뷰 데이터 로드
            if (productSelect.value) {
                loadReviewData(productSelect.value);
            }

            reviewModal.show();
        });
    });

    // 셀렉트 박스에서 다른 상품을 선택했을 때
    if (productSelect) {
        productSelect.addEventListener("change", function () {
            loadReviewData(this.value);
        });
    }

    // 선택된 주문 상품(orderItemId)의 리뷰 작성 여부 조회 및 폼 세팅
    function loadReviewData(orderItemId) {
        modalOrderItemId.value = orderItemId;
        modalReviewId.value = "";
        if (reviewImageInput) {
            reviewImageInput.value = "";
        }

        fetch("/reviews/by-order-item/" + orderItemId)
            .then(function (res) {
                if (!res.ok) {
                    throw new Error("리뷰 정보 조회 실패");
                }
                return res.json();
            })
            .then(function (data) {
                if (data.exists) {
                    // [수정 모드]
                    modalTitle.textContent = "상품 후기 수정";
                    btnSubmit.textContent = "수정하기";
                    reviewForm.action = "/reviews/update";
                    modalReviewId.value = data.reviewId;
                    modalRating.value = data.rating;
                    modalContent.value = data.content;

                    if (data.reviewImage && currentImageNotice) {
                        currentImageNotice.classList.remove("d-none");
                    } else if (currentImageNotice) {
                        currentImageNotice.classList.add("d-none");
                    }
                } else {
                    // [신규 작성 모드]
                    modalTitle.textContent = "상품 후기 작성";
                    btnSubmit.textContent = "등록하기";
                    reviewForm.action = "/reviews";
                    modalRating.value = "5";
                    modalContent.value = "";
                    if (currentImageNotice) {
                        currentImageNotice.classList.add("d-none");
                    }
                }
            })
            .catch(function (err) {
                console.error("후기 정보 로드 에러:", err);
            });
    }

});