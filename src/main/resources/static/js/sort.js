document.addEventListener("DOMContentLoaded", () => {
    const sort = document.querySelector("#sort");
    const url = new URL(window.location.href);

    sort.addEventListener("change", (e) => {
        const sortValue = e.currentTarget.value;

        if(sortValue === 'new'){
            url.searchParams.delete("sort");
        } else {
            url.searchParams.set("sort", sortValue);
        }

        // 정렬 변경 시 첫 페이지로
        url.searchParams.delete("page");
        location.href = url.toString();
    })
})