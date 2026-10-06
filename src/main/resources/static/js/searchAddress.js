document.addEventListener("DOMContentLoaded", function () {

    let zipcode = document.querySelector(".zipcode");
    let basicAddress = document.querySelector(".basicAddress");
    let btnAddress = document.querySelector(".btnAddress");

    btnAddress.addEventListener("click", ()=>{

        new kakao.Postcode({

            oncomplete: function(data) {

                zipcode.value = data.zonecode;
                basicAddress.value = data.address;
            }

        }).open();
    });
});