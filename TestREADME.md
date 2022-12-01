# Sole Trader Identification Test End-Points

### Feature Switches

---

1. [Use stub for submissions to DES](TestREADME.md#post-test-onlycross-regimeregistergrs)
2. [Use stub for generating temporary reference numbers](TestREADME.md#post-test-onlyindividualstrn)

### POST /test-only/cross-regime/register/GRS

---
Stub for downstream Register API. Use the Feature Switch `Use stub for submissions to DES` to use the stub.

To mimic a failure response on the Register API call, `AA222222A` must be used as the nino.

##### Response:
Example Response body:

Status: **OK(200)**
```
{
"identification":{
                  "idType":"SAFEID",
                  "idValue":"X00000123456789"
                 }
}
```

Status: **INTERNAL_SERVER_ERROR(500)**
```
{"failures" : [
     {
       "code": "INVALID_REGIME",
       "reason": "Request has not passed validation.  Invalid regime."
     },
     {
       "code": "INVALID_PAYLOAD",
       "reason": "Request has not passed validation. Invalid payload."
     }
 ]
}
```

### POST /test-only/individuals/trn

---
Stubs the call to Create TRN API #1672

Will always return a successful call

##### Response:
Example Response body:

Status: **CREATED(201)**
```
{"temporaryReferenceNumber" -> 99A99999}
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").