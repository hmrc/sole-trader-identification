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

package uk.gov.hmrc.soletraderidentification.services

import org.mockito.VerifyMacro.Once
import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.test.Helpers._
import uk.gov.hmrc.soletraderidentification.config.AppConfig

import scala.concurrent.ExecutionContext.Implicits.global

class FraudulentNinoListCheckerServiceSpec extends AnyWordSpec with Matchers with IdiomaticMockito with BeforeAndAfterEach {

  implicit val mockAppConfig: AppConfig = mock[AppConfig]

  val allFraudulentNinos: Set[String] = Set("aFraudulentNino", "anotherFraudulentNino")

  object TestFraudulentNinoListCheckerService extends FraudulentNinoListCheckerService()

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAppConfig)
  }

  "give a fraudulentNino, isAFraudulentNino" should {
    "return true" in {

      mockAppConfig.readFraudulentNinosFile returns allFraudulentNinos

      await(TestFraudulentNinoListCheckerService.isAFraudulentNino(allFraudulentNinos.head)) mustBe true
    }
  }

  "give a non fraudulentNino, isAFraudulentNino" should {
    "return false" in {

      mockAppConfig.readFraudulentNinosFile returns allFraudulentNinos

      await(TestFraudulentNinoListCheckerService.isAFraudulentNino("aNonFraudulentNino")) mustBe false
    }
  }

  "isAFraudulentNino" should {
    "cached all fraudulent ninos" in {

      mockAppConfig.readFraudulentNinosFile returns allFraudulentNinos

      val underTest = new FraudulentNinoListCheckerService()

      (1 to 10).foreach(_ => await(underTest.isAFraudulentNino("aNonFraudulentNino")))

      mockAppConfig.readFraudulentNinosFile wasCalled Once
    }
  }


}
