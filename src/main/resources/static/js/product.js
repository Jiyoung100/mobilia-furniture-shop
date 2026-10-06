document.addEventListener("DOMContentLoaded", function() {
    // 썸네일 마우스 오버 이벤트
    let small_img = document.querySelectorAll(".small-img > img");
    let big_img = document.querySelector(".big-img > img");

    small_img.forEach((v) => {
        v.addEventListener('mouseenter', (e) => {
            big_img.src = e.currentTarget.src;
        });
    });

    // 장바구니 추가 이벤트
    const btnCart = document.querySelector(".btnCart");
    const addForm = document.querySelector("#addForm");
    const quantityInput = document.querySelector("#productQuantity");
    const cartQuantity = document.querySelector("#cartQuantity");
    const btnBuy = document.querySelector("#btnBuy");

    // 선택 수량 검사
    function getSelectedQuantity() {
        const quantity = Number(quantityInput.value);
        const maxQuantity = Number(quantityInput.max);

        if (quantity < 1) {
            alert("수량은 1개 이상 선택해주세요.");
            quantityInput.value = 1;
            return null;
        }

        if (quantity > maxQuantity) {
            alert("현재 구매 가능한 최대 수량은 " + maxQuantity + "개입니다.");
            quantityInput.value = maxQuantity;
            return null;
        }

        return quantity;
    }

    // 장바구니
    if (btnCart !== null && addForm !== null) {
        btnCart.addEventListener("click", function() {
            const quantity = getSelectedQuantity();

            if (quantity === null) {
                return;
            }

            cartQuantity.value = quantity;
            addForm.requestSubmit();
        });
    }

    // 바로구매
    if (btnBuy !== null) {
        btnBuy.addEventListener("click", function(event) {
            event.preventDefault();

            const quantity = getSelectedQuantity();

            if (quantity === null) {
                return;
            }

            const productId = btnBuy.dataset.productId;

            location.href =
                "/order?productId=" + productId + "&quantity=" + quantity;
        });
    }

});