/*
 * Copyright 2022 HM Revenue & Customs
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
import stubs.{AuthStub, RegisterWithMultipleIdentifiersStub}
import utils.ComponentSpecHelper

class FraudulentNinoCheckerControllerISpec extends ComponentSpecHelper with AuthStub with RegisterWithMultipleIdentifiersStub {

  "given a fraudulent nino, GET /fraudulent-nino-info" should {
    "return OK with fraudulent info of true" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))

        val result = get("/fraudulent-nino-info/IamAFraudulentNino")

        result.status mustBe OK

        result.json mustBe Json.obj("isAFraudulentNino" -> true)
    }
  }

  "given a non fraudulent nino, GET /fraudulent-nino-info" should {
    "return OK with fraudulent info of false" in {
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))

      val result = get("/fraudulent-nino-info/IamNotAFraudulentNino")

      result.status mustBe OK

      result.json mustBe Json.obj("isAFraudulentNino" -> false)
    }
  }

  "given some stubAuthFailure, GET /fraudulent-nino-info" should {
    "return Unauthorised" in {
      stubAuthFailure()

      get("/fraudulent-nino-info/IamNotAFraudulentNino").status mustBe UNAUTHORIZED
    }
  }

}
