// 메인 슬라이드
const swiper = new Swiper(".mainSwiper", {
    effect: "fade",
    fadeEffect: {
        crossFade: true
    },
    speed: 1500,
    loop: true,
    autoplay: {
        delay: 5000,
        disableOnInteraction: false,
    },
    pagination: {
        el: '.swiper-pagination',
        type: 'progressbar',
    },
});

// 새로 나온 상품 슬라이드
const newProductSwiper = new Swiper(".newProductSwiper", {
    slidesPerView: 4,
    spaceBetween: 24,
    loop: true,
    autoplay: {
        delay: 5000,
        disableOnInteraction: false,
    },
    navigation: {
        nextEl: '.new-product-slider > .swiper-button-next',
        prevEl: '.new-product-slider > .swiper-button-prev',
    },
    breakpoints: {
        0: {
            slidesPerView: 2,
            spaceBetween: 12
        },
        768: {
            slidesPerView: 3,
            spaceBetween: 20
        },
        1200: {
            slidesPerView: 4,
            spaceBetween: 24
        }
    }
});

// 브랜드별 가구 버튼 이벤트
document.querySelectorAll(".brand-section").forEach(section => {
    const buttons = document.querySelectorAll(".brand-tab");
    const panels = document.querySelectorAll(".brand-panel");

    buttons.forEach(button => {
        button.addEventListener("click", () => {
            const selectedBrand = button.dataset.brand;

            // 선택한 버튼의 색상 변경
            buttons.forEach(tab => {
                const isSelected = tab === button;

                tab.classList.toggle("btn-secondary", isSelected);
                tab.classList.toggle("btn-outline-secondary", !isSelected);
                tab.setAttribute("aria-pressed", String(isSelected));
            });

            // 선택한 브랜드의 상품 영역 표시
            panels.forEach(panel => {
                panel.hidden = panel.dataset.brand !== selectedBrand;
            });
        })
    })
})