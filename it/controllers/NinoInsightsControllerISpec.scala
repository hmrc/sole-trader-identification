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

import assets.TestConstants.{testInsightsReturnBody, testInternalId, testNino}
import play.api.libs.json.Json
import play.api.test.Helpers.OK
import stubs.{AuthStub, NinoInsightsStub}
import utils.ComponentSpecHelper

class NinoInsightsControllerISpec extends ComponentSpecHelper with AuthStub with NinoInsightsStub {

  "retrieveNinoInsight" should {
    "return OK with the json response" in {
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
      stubRetrieveNinoInsight(testNino)(OK, testInsightsReturnBody)

      val result = post("/nino-insights")(Json.obj("nino" -> testNino))

      result.status mustBe OK
      result.json mustBe testInsightsReturnBody
    }
  }

}
