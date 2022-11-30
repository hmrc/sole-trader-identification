
# Sole Trader Identification

### How to run the service
1. Make sure any dependent services are running using the following service-manager command `sm --start SOLE_TRADER_IDENTIFICATION_ALL -r`
2. Stop the frontend in service manager using `sm --stop SOLE_TRADER_IDENTIFICATION`
3. Run the frontend locally using
   `sbt 'run 9721 -Dapplication.router=testOnlyDoNotUseInAppConf.Routes'`

## Testing

---
See [TestREADME](TestREADME.md) for more information about test data and endpoints

## End-Points
#### POST /journey

---
Creates a new journeyId and stores it in the database

##### Request:
No body is required for this request

##### Response:
Status: **Created(201)**

Example Response body:

```
{“journeyId”: "<random UUID>"}
```

#### GET /journey/:journeyId

---
Retrieves all the journey data that is stored against a specific journeyID.

##### Request:
A valid journeyId must be sent in the URI

##### Response

| Expected Status                         | Reason                         |
|-----------------------------------------|--------------------------------|
| ```OK(200)```                           | ```JourneyId exists```         |
| ```NOT_FOUND(404)```                    | ```JourneyId does not exist``` |

Example response body:
```
{
"fullName":
    {"firstName":"John",
    "lastName":"Smith"},
"dateOfBirth":"1978-01-05",
"nino":"AA111111A",
"sautr":"1234567890"
"businessVerification": {
    "verificationStatus":"PASS"
  },
"registration": {
    "registrationStatus":"REGISTERED",
    "registeredBusinessPartnerId":"X00000123456789"
  }
}
```

#### GET /journey/:journeyId/:dataKey

---
Retrieves all the journey data that matches the dataKey for a specific journeyID.

##### Request:
Example Request URI

`testJourneyId = <random UUID>`
```
/journey/testJourneyId/nino
```

##### Response:

| Expected Status                         | Reason                                        |
|-----------------------------------------|-----------------------------------------------|
| ```OK(200)```                           | ```JourneyId exists```                        |
| ```NOT_FOUND(404)```                    | ```No data exists for JourneyId or dataKey``` |
| ```FORBIDDEN(403)```                    | ```Auth Internal IDs do not match```          |


Response body for example URI:
```
{"AA111111A"}
```

#### PUT /journey/:journeyId/:dataKey

---
Stores the json body against the data key and journey id provided in the uri

##### Request:
Requires a valid journeyId and user must be authorised to make changes to the data

Example request URI:
`testJourneyId = <random UUID>`
```
/journey/testJourneyId/nino
```

Example request body:
```
{"AA111111A"}
```
##### Response:

| Expected Status                         | Reason                                 |
|-----------------------------------------|----------------------------------------|
| ```OK(200)```                           | ```OK```                               |
| ```FORBIDDEN(403)```                    | ```Auth Internal IDs do not match```   |

#### POST /validate-details

---
Checks if the user entered identifiers match what is held in the database.
This endpoint is feature switched using the `Use stub for Get SA Reference` switch, which returns a specific SAUTR based on the NINO.

##### Request:
Example Body:

```
{
"nino": AA111111A,
"sautr": 1234567890
}
```

##### Response:

| Expected Status                         | Reason                                                          |
|-----------------------------------------|-----------------------------------------------------------------|
| ```OK(200)```                           | ```Identifiers found in database and check returned a result``` |
| ```NOT_FOUND(404)```                    | ```No identifiers found in databse```                           |

Example response bodies:
```
{"matched":true}
```
or
```
{"matched":false}
```
#### POST /register  

___
Submits a registration request to the downstream Register API.
This API is feature switched behind the `Use stub for submissions to DES` switch so it can be stubbed using the Register test endpoint described below.

##### Request:
Body:

```
{
"soleTrader": {
            "nino": "AA111111A",
            "sautr": 1234567890,
            "regime": "VATC"
           }
}
```

The property "regime" is used to define the associated GRS regime. Current valid values
are VATC and PPT.

##### Response:

Status: **OK(200)**
Attempted registration and returns result of call       


Example response bodies:
```
{
"registration":{
       "registrationStatus":"REGISTERED",
       "registeredBusinessPartnerId":"<randomm UUID>"
      }
}
```
or
```
{
"registration":{
       "registrationStatus":"REGISTRATION_FAILED",
       "failures": [
            {
               "code": "INVALID_REGIME",
               "reason": "Request has not passed validation.  Invalid regime."
            }
       ]
      }
}
```

#### DELETE /journey/:journeyId/:dataKey

---
Removes the data that is stored against the dataKey provided for the specific journeyId

##### Request:
Requires a valid journeyId and dataKey

Example request URI:
`testJourneyId = <random UUID>`
```
/journey/remove/testJourneyId/nino
```

##### Response:

| Expected Status                         | Reason                                         |
|-----------------------------------------|------------------------------------------------|
| ```NO_CONTENT(204)```                   | ```Field successfully deleted from database``` |
| ```FORBIDDEN(403)```                    | ```Auth Internal IDs do not match```           |

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
