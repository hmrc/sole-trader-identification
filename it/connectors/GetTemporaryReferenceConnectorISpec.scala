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

package connectors

import assets.TestConstants.{testAddress, testDateOfBirth, testFullName}
import play.api.libs.json.Json
import play.api.test.Helpers._
import stubs.{AuthStub, TemporaryReferenceNumberStub}
import uk.gov.hmrc.http.{HeaderCarrier, InternalServerException}
import uk.gov.hmrc.soletraderidentification.connectors.GetTemporaryReferenceConnector
import uk.gov.hmrc.soletraderidentification.featureswitch.core.config.{FeatureSwitching, TrnStub}
import utils.ComponentSpecHelper

import scala.concurrent.Future

class GetTemporaryReferenceConnectorISpec extends ComponentSpecHelper with AuthStub with TemporaryReferenceNumberStub with FeatureSwitching {

  lazy val connector: GetTemporaryReferenceConnector = app.injector.instanceOf[GetTemporaryReferenceConnector]

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "getTrn" when {
    s"the $TrnStub feature switch is disabled" should {
      "return status Created with the TRN" when {
        "the call to the api was sucessful" in {
          disable(TrnStub)

          stubGetTrn(testDateOfBirth, testFullName, testAddress)(CREATED, Json.obj(
            "temporaryReferenceNumber" -> "99A99999"))
          val result: Future[String] = connector.getTrn(testDateOfBirth, testFullName, testAddress)
          await(result) mustBe "99A99999"
        }
      }
    }
    s"the $TrnStub feature switch is enabled" should {
      "return status Created with the TRN" when {
        "the call to the api was sucessful" in {
          enable(TrnStub)

          stubGetTrn(testDateOfBirth, testFullName, testAddress)(CREATED, Json.obj(
            "temporaryReferenceNumber" -> "99A99999"))
          val result = connector.getTrn(testDateOfBirth, testFullName, testAddress)
          await(result) mustBe ("99A99999")
        }
      }
      "throw an internal server exception" when {
        "the call to the api returns invalid json" in {
          enable(TrnStub)

          stubGetTrn(testDateOfBirth, testFullName, testAddress)(CREATED)
          intercept[InternalServerException](await(connector.getTrn(testDateOfBirth, testFullName, testAddress)))
        }
      }
      "throw an internal server exception" when {
        "the call to the api returns a Bad Request" in {
          enable(TrnStub)

          stubGetTrn(testDateOfBirth, testFullName, testAddress)(BAD_REQUEST)
          intercept[InternalServerException](await(connector.getTrn(testDateOfBirth, testFullName, testAddress)))
        }
      }
    }
  }
}
