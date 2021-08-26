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

import assets.TestConstants._
import play.api.test.Helpers._
import stubs.{AuthStub, RegisterWithMultipleIdentifiersStub}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.soletraderidentification.connectors.RegisterWithMultipleIdentifiersConnector
import uk.gov.hmrc.soletraderidentification.connectors.RegisterWithMultipleIdentifiersHttpParser.RegisterWithMultipleIdentifiersSuccess
import uk.gov.hmrc.soletraderidentification.featureswitch.core.config.{DesStub, FeatureSwitching}
import utils.ComponentSpecHelper

class RegisterWithMultipleIdentifiersConnectorISpec extends ComponentSpecHelper with AuthStub with RegisterWithMultipleIdentifiersStub with FeatureSwitching {

  lazy val connector: RegisterWithMultipleIdentifiersConnector = app.injector.instanceOf[RegisterWithMultipleIdentifiersConnector]

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "registerWithMultipleIdentifiers" when {
    s"the $DesStub feature switch is disabled" when {
      "return OK with status Registered and the SafeId" when {
        "the Registration was a success on the Register API" in {
          disable(DesStub)

          stubRegisterWithNinoSuccess(testNino, testSautr)(OK, testSafeId)
          val result = connector.registerWithNino(testNino, testSautr)
          await(result) mustBe (RegisterWithMultipleIdentifiersSuccess(testSafeId))
        }
      }
    }
    s"the $DesStub feature switch is enabled" when {
      "return OK with status Registered and the SafeId" when {
        "the Registration was a success on the Register API stub" in {
          enable(DesStub)

          stubRegisterWithNinoSuccess(testNino, testSautr)(OK, testSafeId)
          val result = connector.registerWithNino(testNino, testSautr)
          await(result) mustBe (RegisterWithMultipleIdentifiersSuccess(testSafeId))
        }
      }
    }
    s"the $DesStub feature switch is disabled" when {
      "return OK with status Registered and the SafeId" when {
        "the Registration was a success on the Register API with TRN" in {
          disable(DesStub)

          stubRegisterWithTrnSuccess(testTrn, testSautr)(OK, testSafeId)
          val result = connector.registerWithTrn(testTrn, testSautr)
          await(result) mustBe RegisterWithMultipleIdentifiersSuccess(testSafeId)
        }
      }
    }
    s"the $DesStub feature switch is enabled" when {
      "return OK with status Registered and the SafeId" when {
        "the Registration was a success on the Register API with TRN" in {
          enable(DesStub)

          stubRegisterWithTrnSuccess(testTrn, testSautr)(OK, testSafeId)
          val result = connector.registerWithTrn(testTrn, testSautr)
          await(result) mustBe RegisterWithMultipleIdentifiersSuccess(testSafeId)
        }
      }
    }
  }

}
