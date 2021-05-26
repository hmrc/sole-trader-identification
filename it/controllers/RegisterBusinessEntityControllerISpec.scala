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

import assets.TestConstants.{testInternalId, testNino, testSafeId, testSautr}
import javax.inject.Singleton
import play.api.libs.json.Json
import play.api.test.Helpers._
import stubs.{AuthStub, RegisterWithMultipleIdentifiersStub}
import utils.ComponentSpecHelper

@Singleton
class RegisterBusinessEntityControllerISpec extends ComponentSpecHelper with AuthStub with RegisterWithMultipleIdentifiersStub {

  "POST /register" should {
    "return OK with status Registered and the SafeId" when {
      "the Registration was a success" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRegisterWithMultipleIdentifiersSuccess(testNino, testSautr)(OK, testSafeId)

        val jsonBody = Json.obj(
          "soleTrader" -> Json.obj(
            "nino" -> testNino,
            "sautr" -> testSautr
          )
        )

        val resultJson = Json.obj(
          "registration" -> Json.obj(
            "registrationStatus" -> "REGISTERED",
            "registeredBusinessPartnerId" -> testSafeId))

        val result = post("/register")(jsonBody)
        result.status mustBe OK
        result.json mustBe resultJson
      }
    }
    "return INTERNAL_SERVER_ERROR with status Registration_Failed" when {
      "the Registration was not successful" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRegisterWithMultipleIdentifiersFailure(testNino, testSautr)(BAD_REQUEST)

        val jsonBody = Json.obj(
          "soleTrader" -> Json.obj(
            "nino" -> testNino,
            "sautr" -> testSautr
          )
        )

        val result = post("/register")(jsonBody)
        result.status mustBe INTERNAL_SERVER_ERROR

      }
    }
  }

}
