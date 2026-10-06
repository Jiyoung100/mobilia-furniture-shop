document.addEventListener("DOMContentLoaded", function () {
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
});