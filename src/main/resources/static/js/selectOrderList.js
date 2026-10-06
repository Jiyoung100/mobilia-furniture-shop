document.addEventListener("DOMContentLoaded", function () {
    const statusForms = document.querySelectorAll(".status-form");
    statusForms.forEach(function (form) {
        const select = form.querySelector(".status-select");
        let initialStatus = select ? select.value : "";
        const isDelivery = form.classList.contains("delivery-form") || (form.action && form.action.includes("updateDeliveryStatus"));
        const targetName = isDelivery ? "배송상태" : "주문상태";

        form.addEventListener("submit", function (e) {
            const currentStatus = select ? select.value : "";
            if (!confirm(`${targetName}를 '${currentStatus}'(으)로 변경하시겠습니까?`)) {
                e.preventDefault();
            }
        });

        if (select) {
            select.addEventListener("change", function () {
                const currentStatus = this.value;
                if (confirm(`${targetName}를 '${currentStatus}'(으)로 변경하시겠습니까?`)) {
                    form.submit();
                } else {
                    this.value = initialStatus;
                }
            });
        }
    });
});
