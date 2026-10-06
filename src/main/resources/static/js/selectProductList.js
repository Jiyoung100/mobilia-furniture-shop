// HTML 문서의 모든 요소가 준비된 후 상품 선택 기능을 실행하도록 함
document.addEventListener("DOMContentLoaded", function () {

    const selectAll = document.getElementById("chkAll");
    const productCheckboxes = document.querySelectorAll(".product-checkbox");
    const bulkDeleteForm = document.getElementById("bulkDeleteForm");
    const deleteButton = document.getElementById("deleteButton");

    // 체크박스 상태에 따라 삭제 버튼 텍스트와 전체선택 체크박스 상태를 갱신하는 함수
    function updateDeleteButton() {
        const checkedProducts = document.querySelectorAll(".product-checkbox:checked");
        const totalCount = productCheckboxes.length;

        if (totalCount > 0 && checkedProducts.length === totalCount) {
            if (selectAll) selectAll.checked = true;
            if (deleteButton) deleteButton.textContent = "전체 삭제";
        } else {
            if (selectAll) selectAll.checked = false;
            if (deleteButton) deleteButton.textContent = "선택 삭제";
        }
    }

    if (selectAll) {
        selectAll.addEventListener("change", function () {
            productCheckboxes.forEach(function (checkbox) {
                checkbox.checked = selectAll.checked;
            });
            updateDeleteButton();
        });
    }

    // 각 개별 상품 체크박스의 상태가 변경될 때 실행하도록 함
    productCheckboxes.forEach(function (checkbox) {
        checkbox.addEventListener("change", function () {
            updateDeleteButton();
        });
    });

    if (bulkDeleteForm) {
        // 삭제 폼이 제출될 때 실행하도록 함
        bulkDeleteForm.addEventListener("submit", function (e) {
            const checkedProducts = document.querySelectorAll(".product-checkbox:checked");
            const totalCount = productCheckboxes.length;

            if (checkedProducts.length === 0) {
                e.preventDefault();
                alert("삭제할 상품을 선택해주세요.");
                return;
            }

            if (totalCount > 0 && checkedProducts.length === totalCount) {
                if (!confirm("전체 상품을 삭제하시겠습니까?")) {
                    e.preventDefault();
                    return;
                }
                bulkDeleteForm.action = "/products/admin/deleteAll";
            } else {
                if (!confirm("선택한 상품을 삭제하시겠습니까?")) {
                    e.preventDefault();
                    return;
                }
                bulkDeleteForm.action = "/products/admin/delete";
            }
        });
    }

    // 관리 칸의 개별 삭제 버튼 처리
    const singleDeleteButtons = document.querySelectorAll(".single-delete-btn");
    singleDeleteButtons.forEach(function (btn) {
        btn.addEventListener("click", function (e) {
            e.preventDefault();
            const url = this.getAttribute("data-url");
            if (confirm("해당 상품을 삭제하시겠습니까?")) {
                const form = document.createElement("form");
                form.method = "POST";
                form.action = url;
                document.body.appendChild(form);
                form.submit();
            }
        });
    });

    // 정렬 선택 박스 변경 시 이벤트 처리
    const sortSelect = document.getElementById("sort");
    if (sortSelect) {
        sortSelect.addEventListener("change", function () {
            const url = new URL(window.location.href);
            url.searchParams.set("sort", this.value);
            url.searchParams.delete("page");
            window.location.href = url.toString();
        });
    }

    // 초기 상태 반영
    updateDeleteButton();
});
