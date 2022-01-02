var el = document.getElementById("searchProducers");
if(el){
    el.addEventListener("keyup", function(event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            document.getElementById("btnSearchProducers").click();
        }
    });
};
