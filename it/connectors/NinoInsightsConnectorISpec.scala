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

package connectors

import assets.TestConstants.{testInsightsReturnBody, testNino}
import play.api.libs.json.Json
import play.api.test.Helpers.{BAD_REQUEST, OK, await, defaultAwaitTimeout}
import stubs.NinoInsightsStub
import uk.gov.hmrc.http.{HeaderCarrier, InternalServerException}
import uk.gov.hmrc.soletraderidentification.connectors.NinoInsightsConnector
import uk.gov.hmrc.soletraderidentification.featureswitch.core.config.{FeatureSwitching, InsightStub}
import utils.ComponentSpecHelper

class NinoInsightsConnectorISpec extends ComponentSpecHelper with FeatureSwitching with NinoInsightsStub {

  lazy val connector: NinoInsightsConnector = app.injector.instanceOf[NinoInsightsConnector]

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "getNinoInsights" should {
    "return json body" in {
      disable(InsightStub)
      stubRetrieveNinoInsight(testNino)(OK, testInsightsReturnBody)

      val result = connector.retrieveNinoInsight(testNino)

      await(result) mustBe testInsightsReturnBody
    }
    "throw an exception" when {
      "an unexpected status code is returned" in {
        disable(InsightStub)
        stubRetrieveNinoInsight(testNino)(BAD_REQUEST, Json.obj())

        intercept[InternalServerException](
          await(connector.retrieveNinoInsight(testNino))
        )
      }
      "the json validation fails" in {
        disable(InsightStub)
        when(method = POST, uri = "/nino-insights-proxy/check/insights", body = Json.obj("nino" -> testNino)).thenReturn(OK, "test")

        intercept[InternalServerException](
          await(connector.retrieveNinoInsight(testNino))
        )
      }
    }
  }

}
