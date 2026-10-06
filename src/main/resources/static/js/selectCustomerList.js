// HTML 문서의 모든 요소가 준비된 후 회원 선택 및 삭제 기능을 실행하도록 함
document.addEventListener("DOMContentLoaded", function () {

    const selectAll = document.getElementById("chkAll");
    const customerCheckboxes = document.querySelectorAll(".customer-checkbox");
    const bulkDeleteForm = document.getElementById("bulkDeleteForm");
    const deleteButton = document.getElementById("deleteButton");

    // 체크박스 상태에 따라 삭제 버튼 텍스트와 전체선택 체크박스 상태를 갱신하는 함수
    function updateDeleteButton() {
        const checkedCustomers = document.querySelectorAll(".customer-checkbox:checked");
        const totalCount = customerCheckboxes.length;

        if (totalCount > 0 && checkedCustomers.length === totalCount) {
            if (selectAll) selectAll.checked = true;
            if (deleteButton) deleteButton.textContent = "전체 삭제";
        } else {
            if (selectAll) selectAll.checked = false;
            if (deleteButton) deleteButton.textContent = "선택 삭제";
        }
    }

    if (selectAll) {
        selectAll.addEventListener("change", function () {
            customerCheckboxes.forEach(function (checkbox) {
                checkbox.checked = selectAll.checked;
            });
            updateDeleteButton();
        });
    }

    // 각 개별 회원 체크박스의 상태가 변경될 때 실행하도록 함
    customerCheckboxes.forEach(function (checkbox) {
        checkbox.addEventListener("change", function () {
            updateDeleteButton();
        });
    });

    if (bulkDeleteForm) {
        bulkDeleteForm.addEventListener("submit", function (e) {
            const checkedCustomers = document.querySelectorAll(".customer-checkbox:checked");
            const totalCount = customerCheckboxes.length;

            if (checkedCustomers.length === 0) {
                e.preventDefault();
                alert("삭제할 회원을 선택해주세요.");
                return;
            }

            if (totalCount > 0 && checkedCustomers.length === totalCount) {
                if (!confirm("전체 회원을 삭제하시겠습니까?")) {
                    e.preventDefault();
                    return;
                }
                bulkDeleteForm.action = "/customers/admin/deleteAll";
            } else {
                if (!confirm("선택한 회원을 삭제하시겠습니까?")) {
                    e.preventDefault();
                    return;
                }
                bulkDeleteForm.action = "/customers/admin/delete";
            }
        });
    }

    // 관리 칸의 개별 삭제 버튼 처리
    const singleDeleteButtons = document.querySelectorAll(".single-delete-btn");
    singleDeleteButtons.forEach(function (btn) {
        btn.addEventListener("click", function (e) {
            e.preventDefault();
            const url = this.getAttribute("data-url");
            if (confirm("해당 회원을 삭제하시겠습니까?")) {
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
