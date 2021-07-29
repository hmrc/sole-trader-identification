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

package repositories

import assets.TestConstants.{testInternalId, testJourneyId}
import play.api.libs.json.{JsString, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.soletraderidentification.models.JourneyDataModel
import uk.gov.hmrc.soletraderidentification.repositories.JourneyDataRepository
import uk.gov.hmrc.soletraderidentification.repositories.JourneyDataRepository._
import utils.ComponentSpecHelper
import reactivemongo.play.json.JsObjectDocumentWriter
import scala.concurrent.ExecutionContext.Implicits.global

class JourneyDataRepositoryISpec extends ComponentSpecHelper {

  val repo: JourneyDataRepository = app.injector.instanceOf[JourneyDataRepository]

  override def beforeEach(): Unit = {
    super.beforeEach()
    await(repo.drop)
  }

  val authInternalIdKey: String = "authInternalId"
  val creationTimestampKey: String = "creationTimestamp"

  "createJourney" should {
    "successfully insert the journeyId" in {
      await(repo.createJourney(testJourneyId, testInternalId))
      await(repo.findById(testJourneyId)) mustBe Some(JourneyDataModel(testJourneyId))
    }
  }
  s"getJourneyData($testJourneyId)" should {
    "successfully return all data" in {
      await(repo.createJourney(testJourneyId, testInternalId))
      await(repo.getJourneyData(testJourneyId, testInternalId)).map(_.-(creationTimestampKey)) mustBe Some(Json.obj(authInternalIdKey -> testInternalId))
    }
  }
  "updateJourneyData" should {
    "successfully insert data" in {
      val testKey = "testKey"
      val testData = "test"
      await(repo.createJourney(testJourneyId, testInternalId))
      await(repo.updateJourneyData(testJourneyId, testKey, JsString(testData), testInternalId))
      await(repo.getJourneyData(testJourneyId, testInternalId)).map(json => (json \ testKey).as[String]) mustBe Some(testData)
    }
    "successfully update data when data is already stored against a key" in {
      val testKey = "testKey"
      val testData = "test"
      val updatedData = "updated"
      await(repo.createJourney(testJourneyId, testInternalId))
      await(repo.updateJourneyData(testJourneyId, testKey, JsString(testData), testInternalId))
      await(repo.updateJourneyData(testJourneyId, testKey, JsString(updatedData), testInternalId))
      await(repo.getJourneyData(testJourneyId, testInternalId)).map(json => (json \ testKey).as[String]) mustBe Some(updatedData)
    }

  }
  "removeJourneyDataField" should {
    "successfully remove a field" in {
      val testKey = "testKey"
      val testData = "test"

      await(repo.createJourney(testJourneyId, testInternalId))
      await(repo.updateJourneyData(testJourneyId, testKey, JsString(testData), testInternalId))
      await(repo.removeJourneyDataField(testJourneyId, testInternalId, testKey))
      await(repo.getJourneyData(testJourneyId, testInternalId)).map(_.-(creationTimestampKey)) mustBe Some(Json.obj(authInternalIdKey -> testInternalId))
    }
    "pass successfully when the field is not present" in {
      val testKey = "testKey"
      val testData = "test"
      val testSecondKey = "secondKey"

      await(repo.createJourney(testJourneyId, testInternalId))
      await(repo.updateJourneyData(testJourneyId, testKey, JsString(testData), testInternalId))
      await(repo.removeJourneyDataField(testJourneyId, testInternalId, testSecondKey))
      await(repo.getJourneyData(testJourneyId, testInternalId)).map(_.-(creationTimestampKey)) mustBe Some(Json.obj(authInternalIdKey -> testInternalId, testKey -> testData))
    }
  }
  "removeJourneyData" should {
    "successfully remove data associated with journeyId" in {
      val json = Json.obj(JourneyIdKey -> testJourneyId,
        AuthInternalIdKey -> testInternalId,
        "FullName" -> Json.obj("firstName" -> "John", "lastName" -> "Smith"))

      await(repo.collection.insert.one(json))
      await(repo.removeJourneyData(testJourneyId, testInternalId))
      await(repo.getJourneyData(testJourneyId, testInternalId)).map(_.-(creationTimestampKey)) mustBe Some(Json.obj(authInternalIdKey -> testInternalId))

    }
  }
}
