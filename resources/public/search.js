var producers = document.getElementById("searchProducers");
var products = document.getElementById("searchProducts");
var producerReviews = document.getElementById("searchProducerReviews");

if(producers){
    producers.addEventListener("keyup", function(event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            document.getElementById("btnSearchProducers").click();
        }
    });
};

if(products){
    products.addEventListener("keyup", function(event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            document.getElementById("btnSearchProducts").click();
        }
    });
};

if(producerReviews){
    producerReviews.addEventListener("keyup", function(event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            document.getElementById("btnSearchProducerReviews").click();
        }
    });
};
