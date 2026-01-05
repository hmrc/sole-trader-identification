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
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.Mockito._
import play.api.libs.json.{JsString, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.soletraderidentification.repositories.JourneyDataRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class JourneyDataServiceSpec extends AnyWordSpec with Matchers with MockitoSugar {

  val mockJourneyDataRepository: JourneyDataRepository = mock[JourneyDataRepository]
  val mockJourneyIdGenerationService: JourneyIdGenerationService = mock[JourneyIdGenerationService]

  object TestJourneyDataService extends JourneyDataService(mockJourneyDataRepository, mockJourneyIdGenerationService)

  val testJourneyId = "testJourneyId"
  val testInternalId = "testInternalId"

  "createJourney" should {
    "call to store a new journey with the generated journey ID" in {
      when(mockJourneyIdGenerationService.generateJourneyId()).thenReturn(testJourneyId)
      when(mockJourneyDataRepository.createJourney(testJourneyId, testInternalId)).thenReturn(Future.successful(testJourneyId))

      await(TestJourneyDataService.createJourney(testInternalId)) mustBe testJourneyId
    }
  }

  "getJourneyData" should {
    "return the stored journey data" when {
      "the data exists in the database" in {
        val testJourneyData = Json.obj("testKey" -> "testValue")

        when(mockJourneyDataRepository.getJourneyData(testJourneyId, testInternalId)).thenReturn(Future.successful(Some(testJourneyData)))

        await(TestJourneyDataService.getJourneyData(testJourneyId, testInternalId)) mustBe Some(testJourneyData)
      }
    }
    "return None" when {
      "the data does not exist in the database" in {
        when(mockJourneyDataRepository.getJourneyData(testJourneyId, testInternalId)).thenReturn(Future.successful(None))

        await(TestJourneyDataService.getJourneyData(testJourneyId, testInternalId)) mustBe None
      }
    }
  }

  "getJourneyDataByKey" should {
    "return the stored journey data for the key provided" when {
      "the data exists in the database" in {
        val testKey = "testKey"
        val testValue = "testValue"

        val testJourneyData = Json.obj(testKey -> testValue)

        when(mockJourneyDataRepository.getJourneyData(testJourneyId, testInternalId)).thenReturn(Future.successful(Some(testJourneyData)))

        await(TestJourneyDataService.getJourneyDataByKey(testJourneyId, testKey, testInternalId)) mustBe Some(JsString(testValue))

      }
    }
    "return None" when {
      "the data does not exist in the database" in {
        val testKey = "testKey"

        when(mockJourneyDataRepository.getJourneyData(testJourneyId, testInternalId)).thenReturn(Future.successful(None))

        await(TestJourneyDataService.getJourneyDataByKey(testJourneyId, testKey, testInternalId)) mustBe None
      }
    }
  }

  "updateJourneyData" should {

    val testKey = "testKey"
    val testValue = JsString("testValue")

    "return true when the data field is successfully updated" in {

      when(mockJourneyDataRepository.updateJourneyData(testJourneyId, testInternalId, testKey, testValue)).thenReturn(Future.successful(true))

      await(TestJourneyDataService.updateJourneyData(testJourneyId, testInternalId, testKey, testValue)) mustBe true

    }

    "return false when an update fails" in {

      when(mockJourneyDataRepository.updateJourneyData(testJourneyId, testInternalId, testKey, testValue)).thenReturn(Future.successful(false))

      await(TestJourneyDataService.updateJourneyData(testJourneyId, testInternalId, testKey, testValue)) mustBe false

    }
  }

  "removeJourneyDataField" should {

    val testKey = "testKey"

    "return true when a data field is successfully removed" in {

      when(mockJourneyDataRepository.removeJourneyDataField(testJourneyId, testInternalId, testKey)).thenReturn(Future.successful(true))

      await(TestJourneyDataService.removeJourneyDataField(testJourneyId, testInternalId, testKey)) mustBe true

    }

    "return false when a data field is not successfully removed" in {

      when(mockJourneyDataRepository.removeJourneyDataField(testJourneyId, testInternalId, testKey)).thenReturn(Future.successful(false))

      await(TestJourneyDataService.removeJourneyDataField(testJourneyId, testInternalId, testKey)) mustBe false

    }
  }

  "removeJourneyData" should {
    "return true" when {
      "the data field exist and has been removed" in {

        when(mockJourneyDataRepository.removeJourneyData(testJourneyId, testInternalId)).thenReturn(Future.successful(true))

        await(TestJourneyDataService.removeJourneyData(testJourneyId, testInternalId)) mustBe true
      }
    }

    "return false" when {
      "the data field does not exist" in {

        when(mockJourneyDataRepository.removeJourneyData(testJourneyId, testInternalId)).thenReturn(Future.successful(false))

        await(TestJourneyDataService.removeJourneyData(testJourneyId, testInternalId)) mustBe false
      }
    }
  }

}
