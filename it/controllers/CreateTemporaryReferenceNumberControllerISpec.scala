/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import assets.TestConstants._
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import play.api.test.Helpers._
import stubs.{AuthStub, CreateTemporaryReferenceNumberStub}
import utils.ComponentSpecHelper

class CreateTemporaryReferenceNumberControllerISpec extends ComponentSpecHelper with AuthStub with CreateTemporaryReferenceNumberStub {

  "POST /individuals/trn" should {
    "return 201 created with a trn" when {
      "a TRN was successfully created" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubCreateTemporaryReferenceNumber(testDateOfBirth, testFullName, testAddress)(CREATED, Json.obj("temporaryReferenceNumber" -> "99A99999"))

        val jsonBody = Json.obj(
          "dateOfBirth" -> testDateOfBirth,
            "fullName" -> testFullName,
            "address" -> testAddress
          )

        val resultJson = Json.obj("temporaryReferenceNumber" -> "99A99999")

        val result: WSResponse = post("/get-trn")(jsonBody)

        result.status mustBe CREATED
        result.json mustBe resultJson
      }
    }
  }

}
