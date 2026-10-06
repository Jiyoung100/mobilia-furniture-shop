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