# product-service
This project provides REST services
To Get Product information:
1) Service will accept productId and will render product name and current price for the item.
2) Responds to an HTTP GET request at /products/{id} and delivers product data as JSON (where {id} will be a number. 
3) Example product IDs: 15117729, 16483589, 16696652, 16752456, 15643793) 
4)	Example response: {"id":13860428,"name":"The Big Lebowski (Blu-ray) (Widescreen)","current_price":{"value": 13.49,"currency_code":"USD"}}
5) Performs an HTTP GET to retrieve the product name 
6) Reads pricing information from a NoSQL data store and combines it with the product id and name from the HTTP request into a single response. 

To update pricing information
1) Accepts HTTP PUT to insert/update product current price.
2) Example request body:
  {
  "id":12345,
  "price":90.00,
  "currency":"USD"
}
3) It updates Cassandra "price" table in "catalog" keyspace.
