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
import org.scalatest.concurrent.TimeLimits
import org.scalatest.time.{Seconds, Span}
import play.api.libs.json.Json
import play.api.test.Helpers._
import stubs.{AuthStub, RegisterWithMultipleIdentifiersStub}
import utils.ComponentSpecHelper

class FraudulentNinoCheckerControllerPerformanceISpec extends ComponentSpecHelper with AuthStub with RegisterWithMultipleIdentifiersStub with TimeLimits {

  "GET /fraudulent-nino-info" should {
    "be ble to load 53,649 lines file 500 times below 8 secs (it reads the file only 1 time. It does not reload the ninos file every time)" in {
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))

      failAfter(Span(8, Seconds)) {

        (1 to 500).foreach(_ => {

          val result = get("/fraudulent-nino-info/IamAFraudulentNino")

          result.json mustBe Json.obj("isAFraudulentNino" -> true)

        })
      }

    }

  }
}
