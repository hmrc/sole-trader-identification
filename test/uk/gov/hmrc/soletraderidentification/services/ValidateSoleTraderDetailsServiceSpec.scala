/*
 * Copyright 2024 HM Revenue & Customs
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

import org.scalatestplus.mockito.MockitoSugar
import org.mockito.ArgumentMatchers.{eq => eqTo}
import org.mockito.Mockito._
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.soletraderidentification.connectors.GetSaReferenceConnector
import uk.gov.hmrc.soletraderidentification.models.{DetailsMatched, DetailsMismatched, DetailsNotFound}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ValidateSoleTraderDetailsServiceSpec extends AnyWordSpec with Matchers with MockitoSugar {
  val mockGetSaReferenceConnector: GetSaReferenceConnector = mock[GetSaReferenceConnector]

  object TestValidateIncorporateEntityDetailsService extends ValidateSoleTraderDetailsService(
    mockGetSaReferenceConnector
  )

  implicit val hc: HeaderCarrier = HeaderCarrier()
  val testNino = "testNino"
  val testSAUTR = "testSAUTR"

  "validateDetails" should {
    s"return $DetailsMatched" when {
      "the supplied SA Reference matches the stored SA Reference" in {
        when(mockGetSaReferenceConnector.getSaReference(eqTo(testNino))(using eqTo(hc))).thenReturn(Future.successful(Some(testSAUTR)))

        await(TestValidateIncorporateEntityDetailsService.validateDetails(testNino, testSAUTR)) mustBe DetailsMatched
      }
    }
    s"return $DetailsMismatched" when {
      "the supplied SA Reference does not match the stored SA Reference" in {
        val mismatchedTestCtReference = "mismatchedTestCtReference"

        when(mockGetSaReferenceConnector.getSaReference(eqTo(testNino))(using eqTo(hc))).thenReturn(Future.successful(Some(testSAUTR)))

        await(TestValidateIncorporateEntityDetailsService.validateDetails(testNino, mismatchedTestCtReference)) mustBe DetailsMismatched
      }
    }
    s"return $DetailsNotFound" when {
      "there is no stored SA Reference for the provided nino" in {
        when(mockGetSaReferenceConnector.getSaReference(eqTo(testNino))(using eqTo(hc))).thenReturn(Future.successful(None))

        await(TestValidateIncorporateEntityDetailsService.validateDetails(testNino, testSAUTR)) mustBe DetailsNotFound
      }
    }

  }


}
