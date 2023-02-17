/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.test.Helpers._
import stubs.{AuthStub, GetSaReferenceStub}
import utils.ComponentSpecHelper

class ValidateSoleTraderDetailsControllerISpec extends ComponentSpecHelper with GetSaReferenceStub with AuthStub {

  "validateDetails" should {
    "return details match" when {
      "supplied details match those in database" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubGetSaReference(testNino)(status = OK, body = Json.obj("SAUTR" -> testSautr))
        val testJson = Json.obj("matched" -> true)
        val suppliedJson = Json.obj(
          "nino" -> testNino,
          "sautr" -> testSautr
        )

        val result = post("/validate-details")(suppliedJson)

        result.status mustBe OK
        result.json mustBe testJson
      }
    }

    "return details do not match" when {
      "supplied details do not match those in database" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubGetSaReference(testNino)(status = OK, body = Json.obj("SAUTR" -> testSautr))
        val testJson = Json.obj("matched" -> false)
        val suppliedJson = Json.obj(
          "nino" -> testNino,
          "sautr" -> "mismatch"
        )

        val result = post("/validate-details")(suppliedJson)

        result.status mustBe OK
        result.json mustBe testJson
      }
    }

    "return details not found" when {
      "supplied details are not found in database" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubGetSaReference("000000000")(status = NOT_FOUND)
        val testJson = Json.obj(
          "code" -> "NOT_FOUND",
          "reason" -> "The back end has indicated that SA UTR cannot be returned"
        )

        val suppliedJson = Json.obj(
          "nino" -> "000000000",
          "sautr" -> testSautr
        )

        val result = post("/validate-details")(suppliedJson)

        result.status mustBe BAD_REQUEST
        result.json mustBe testJson
      }
    }
    "return Unauthorised" when {
      "there is an auth failure" in {
        stubAuthFailure()
        stubGetSaReference(testNino)(status = OK, body = Json.obj("SAUTR" -> testSautr))

        val suppliedJson = Json.obj(
          "nino" -> testNino,
          "sautr" -> testSautr
        )

        val result = post("/validate-details")(suppliedJson)

        result.status mustBe UNAUTHORIZED

      }
    }
  }

}
