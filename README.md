# Bee Organic

Clojure web application for purposes of exam "Alati i metode softverskog inženjerstva".
Bee organic gives important information about producers and products made from organic honey. 

Application has two roles - admin and users. 
Admin has to be logged in application in order to change data. Functionalities for admin are:
- view all producers
- add new producer
- edit producer
- delete producer
- view all products
- add new product
- edit product
- delete product
- view all producer-reviews
- view reviews for producer
- add producer-review
- delete producer-reviews
- view all product-reviews
- view reviews for product
- add product-review
- delete product-reviews
- search producers, products, producer-reviews, product-reviews
- filter producers by certificates (Organic certificate, No certificate or In conversion period)
- filter products by packaging (Plastic or Glass packaging) and producers
- filter reviews by rating
- login and logout

Users that are not admin cannot add,edit or delete data except for reviews which they can add for a certain product or producer. In reviews users can also rate. As a result, every product and producer has average rating which can be useful to other users.

The application uses following libraries:
- [Compojure](https://github.com/weavejester/compojure)
- [Ring](https://github.com/ring-clojure/ring)
- [Hiccup](https://github.com/weavejester/hiccup)
- [Monger](https://github.com/michaelklishin/monger)
- [Markdown-clj](https://github.com/yogthos/markdown-clj)

Backend language used in the application is Clojure. For fronted are used HTML, CSS, JavaSript and Bootstrap. Database used in this application is MongoDB.

Application structure and login/logout functions are used from Udemy course that is linked in References. 

## Prerequisites

You will need [MongoDB](https://www.mongodb.com/) and [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run following command from root folder:

    lein ring server
    
## References
- Daniel Higginbotham (2015), [Clojure for the Brave and True](https://www.amazon.com/Clojure-Brave-True-Ultimate-Programmer/dp/1593275919)
- [Monger documentation](http://clojuremongodb.info/articles/getting_started.html)
- [Clojure - from beginner to advanced](https://www.udemy.com/course/advancedclojure/)
## License

Copyright © 2021 Djina Djoric
