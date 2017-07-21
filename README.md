# REST API

```
Root url: localhost:8080/api/v1/
```

<b>Create a transaction</b>

```
POST /transactions

{"transactionDate":"21/7/16 12:00", "transactionAmount":20, "currencyCode":2}

note* transactionId, createdDate, and modifiedDate (when updated) will be created automatically by the server
```

<b>Update a transaction</b>

```
PUT /transactions/{id} 

{"currencyCode":4}
```

<b>Delete a transaction</b>

```
DELETE /transactions/{id} 
```

<b>Get a transaction by id</b>

```
GET /transactions/{id} 
```

<b>Get all transactions</b>

```
GET /transactions 
```

In order to keep it simple and reduce configuration, as per rules 1, 2 and 3, I decided to use a .txt file (transactions.txt) to store transactions (in JSON format) instead of using a database.


<b>Assumptions I made:</b>

  * transactionID is created automatically (incremented) when a transaction is created.
  * createdDate is created automatically when a transaction is created.
  * modifiedDate is created or updated when a record is updated

<b>Technologies/tools used</b>
  * Maven
  * Java
  * Spring Boot
  * GSON (Google library for converting objects to/from JSON)
  * Postman (REST client)

The project took ~5 hours, including learning time, implementation, testing, documenting. I would have liked to have written more tests, but I feel like I had reached the time limit.

I have deployed the application on Heroku, so the API can now be reached at:

`https://salty-caverns-46525.herokuapp.com/api/v1`

E.g 

`https://salty-caverns-46525.herokuapp.com/api/v1/transactions/1`

(The server shuts down after an hour or so of inactivity, so the first request will take a few seconds)
