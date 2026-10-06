const category1 = document.getElementById("select-category1");
const category2 = document.getElementById("select-category2");

const categoryData = {
    "거실": ["소파", "수납장", "거실테이블"],
    "다이닝": ["식탁", "식탁의자", "거실장"],
    "침실": ["침대", "옷장", "침구"],
    "욕실": ["세면대하부장", "욕실 시스템", "샤워용품"],
    "키즈룸": ["영유아 가구", "장난감 정리함", "영유아 침대"],
    "홈오피스": ["컴퓨터책상", "의자", "책상조명"],
    "현관": ["신발장", "수납함", "옷걸이 행거"],
    "베란다": ["야외용 테이블", "야외용 식탁 의자", "야외용 소파"]
};

category1.addEventListener("change", function () {

    category2.innerHTML =
        '<option value="">품목 분류를 선택해주세요.</option>';

    const selectedCategory = categoryData[this.value];

    if (selectedCategory) {
        selectedCategory.forEach(function (item) {

            const option = document.createElement("option");

            option.value = item;
            option.textContent = item;

            category2.appendChild(option);
        });
    }
});
category1.value = "";
category2.innerHTML = '<option value="">품목 분류를 선택해주세요.</option>';