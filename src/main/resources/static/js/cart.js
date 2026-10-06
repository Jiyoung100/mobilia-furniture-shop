document.addEventListener("DOMContentLoaded", function () {
    // 고객 ID
    const customerId = document.querySelector("#customerId");
    // 수량 변경 및 삭제 요청용 form
    const cartForm = document.querySelector("#cartActionForm");
    const mappingMethod = document.querySelector("#mappingMethod");
    // 수량 입력창
    const cartQuantity = document.querySelectorAll(".cart-quantity");
    // 삭제 버튼
    const btnDelete = document.querySelectorAll(".btnDelete");
    const btnCartAllDelete = document.querySelector(".btnCartAllDelete");
    // 체크박스
    const chkAll = document.querySelector(".chkAll");
    const chkOne = document.querySelectorAll(".chkOne");

    // 총 상품금액 / 총 주문금액
    const totalProductPrice = document.querySelector(".totalProductPrice");
    const totalOrderPrice = document.querySelector(".totalOrderPrice");

    // 장바구니가 비어 있으면 실행하지 않음
    if (customerId === null || cartForm === null || mappingMethod === null || cartQuantity.length === 0) {
        return;
    }

    // PUT / DELETE 요청 공통 처리
    function submitCart(method, action) {
        mappingMethod.value = method;
        cartForm.action = action;
        cartForm.submit();
    }

    // 1. 수량 변경
    cartQuantity.forEach((input) => {
        input.addEventListener("change", function () {
            const quantity = Number(input.value);
            const cartItemId = input.dataset.id;
            const stock = Number(input.dataset.stock);
            if (input.value.trim() === "" || !Number.isInteger(quantity) || quantity < 1) {
                alert("수량은 1개 이상이어야합니다.");
                location.reload();
                return;
            }
            if (quantity > stock) {
                alert("상품의 재고가 부족합니다. (현재 재고: " + stock + "개)");
                location.reload();
                return;
            }
            submitCart( "put", "/cart/update/" + quantity + "/" + customerId.value + "/" + cartItemId);
        });
    });

    // 2. 개별 삭제
    btnDelete.forEach((button) => {
        button.addEventListener("click", function () {
            if (!confirm("해당 상품을 삭제하시겠습니까?")) {
                return;
            }
            const cartItemId = button.dataset.id;
            submitCart( "delete", "/cart/removeone/" + customerId.value + "/" + cartItemId );
        });
    });

    // 선택 상품 총 금액 계산
    function updateTotalPrice() {
        let totalPrice = 0;

        const checkedItems = document.querySelectorAll(".chkOne:checked");

        checkedItems.forEach((checkbox) => {
            const cartItem = checkbox.closest(".cart-item");
            totalPrice += Number(cartItem.dataset.totalPrice);
        });

        const formattedPrice = totalPrice.toLocaleString("ko-KR") + "원";

        totalProductPrice.textContent = formattedPrice;
        totalOrderPrice.textContent = formattedPrice;
    }

    // 3. 체크박스 업데이트
    function updateCheckbox() {
        const checkedItems = document.querySelectorAll(".chkOne:checked");
        const checkedCount = checkedItems.length;

        // 모든 상품이 선택되었는지 확인
        chkAll.checked = checkedCount === chkOne.length;
        // 삭제 버튼 문구 변경
        if (checkedCount === chkOne.length) {
            btnCartAllDelete.textContent = "전체 삭제";
        } else {
            btnCartAllDelete.textContent = "선택 삭제";
        }

        // 선택 상품 총 금액 변경
        updateTotalPrice();
    }

    // 전체 선택 / 전체 해제
    chkAll.addEventListener("change", function () {
        chkOne.forEach((checkbox) => {
            checkbox.checked = chkAll.checked;
        });
        updateCheckbox();
    });

    // 체크박스 선택
    chkOne.forEach((checkbox) => {
        checkbox.addEventListener("change", function () {
            updateCheckbox();
        });
    });

    // 4. 전체 / 선택 삭제
    btnCartAllDelete.addEventListener("click", function () {
        const checkedItems = document.querySelectorAll(".chkOne:checked");

        // 선택한 상품이 없으면 종료
        if (checkedItems.length === 0) {
            alert("삭제할 상품을 선택해 주세요.");
            return;
        }
        // 모든 상품을 선택했을 경우 → 전체 삭제
        if (checkedItems.length === chkOne.length) {
            if (!confirm("장바구니의 모든 상품을 삭제하시겠습니까?")) {
                return;
            }
            submitCart("delete", "/cart/removeall/" + customerId.value );
            return;
        }
        // 일부 상품만 선택한 경우 → 선택 삭제
        if (!confirm("선택한 상품을 삭제하시겠습니까?")) {
            return;
        }

        const ids = [];
        checkedItems.forEach(checkbox => {
            ids.push(checkbox.value);
        });
        submitCart("delete", "/cart/removefew/" + customerId.value + "/" + ids.join(",") );
    });

    // 5. 주문 처리 (전체 주문 / 개별 주문)
    const btnOrderSelect = document.querySelector(".btnOrderSelect");
    const btnOrderOne = document.querySelectorAll(".btnOrderOne");
    const btnOrderAll = document.querySelector(".btnOrderAll");

    // 장바구니 주문 전송 함수
    function submitOrderSheet(cartItemIds) {
        if (!cartItemIds || cartItemIds.length === 0) {
            alert("주문할 상품을 선택해주세요.");
            return;
        }

        // 동적으로 POST Form 생성 후 전송
        const form = document.createElement("form");
        form.method = "post";
        form.action = "/order/sheet";

        cartItemIds.forEach(id => {
            const input = document.createElement("input");
            input.type = "hidden";
            input.name = "cartItemIds";
            input.value = id;
            form.appendChild(input);
        });

        document.body.appendChild(form);
        form.submit();
    }

    // [선택 상품 주문]
    if (btnOrderSelect) {
        btnOrderSelect.addEventListener("click", function () {
            const checkedItems = document.querySelectorAll(".chkOne:checked");
            if (checkedItems.length === 0) {
                alert("주문할 상품을 1개 이상 선택해 주세요.");
                return;
            }

            // 선택된 .cart-item들을 하나씩 순회하며 재고 검사
            for (let chk of checkedItems) {
                const row = chk.closest(".cart-item");
                const qtyInput = row.querySelector(".cart-quantity");
                const nameTag = row.querySelector(".cart-product-info h3 a");

                const quantity = Number(qtyInput.value);
                const stock = Number(qtyInput.dataset.stock); // cart.html의 th:data-stock 읽기
                const name = nameTag ? nameTag.textContent.trim() : "선택 상품";

                if (!isNaN(stock) && quantity > stock) {
                    alert(`[${name}] 상품의 재고가 부족합니다.\n(현재 재고: ${stock}개, 선택 수량: ${quantity}개)`);
                    qtyInput.focus();
                    return; // 주문서 화면 이동 차단
                }
            }

            const ids = Array.from(checkedItems).map(chk => chk.value);
            submitOrderSheet(ids);
        });
    }

    // [전체 상품 주문]
    if (btnOrderAll) {
        btnOrderAll.addEventListener("click", function() {
            // 장바구니 내 모든 체크박스 행 검사
            for (let chk of chkOne) {
                const row = chk.closest(".cart-item");
                const qtyInput = row.querySelector(".cart-quantity");
                const nameTag = row.querySelector(".cart-product-info h3 a");

                const quantity = Number(qtyInput.value);
                const stock = Number(qtyInput.dataset.stock);
                const name = nameTag ? nameTag.textContent.trim() : "해당 상품";

                if (!isNaN(stock) && quantity > stock) {
                    alert(`[${name}] 상품의 재고가 부족합니다.\n(현재 재고: ${stock}개, 선택 수량: ${quantity}개)`);
                    qtyInput.focus();
                    return; // 주문서 화면 이동 차단
                }
            }

            const ids = Array.from(chkOne).map(chk => chk.value);
            submitOrderSheet(ids);
        });
    }

    // [개별 바로구매 버튼]
    btnOrderOne.forEach(button => {
        button.addEventListener("click", function () {
            const row = button.closest(".cart-item");
            const chk = row.querySelector(".chkOne");
            const qtyInput = row.querySelector(".cart-quantity");
            const nameTag = row.querySelector(".cart-product-info h3 a");

            if (qtyInput) {
                const quantity = Number(qtyInput.value);
                const stock = Number(qtyInput.dataset.stock);
                const name = nameTag ? nameTag.textContent.trim() : "해당 상품";

                if (!isNaN(stock) && quantity > stock) {
                    alert(`[${name}] 상품의 재고가 부족합니다.\n(현재 재고: ${stock}개, 선택 수량: ${quantity}개)`);
                    qtyInput.focus();
                    return; // 주문서 화면 이동 차단
                }
            }

            if (chk) {
                submitOrderSheet([chk.value]);
            }
        });
    });
});