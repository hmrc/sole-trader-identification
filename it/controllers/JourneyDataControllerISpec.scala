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

package controllers

import assets.TestConstants.{testInternalId, testJourneyId}
import play.api.libs.json.{JsString, Json}
import play.api.test.Helpers._
import stubs.AuthStub
import utils.{ComponentSpecHelper, CustomMatchers, JourneyDataMongoHelper}

class JourneyDataControllerISpec extends ComponentSpecHelper with CustomMatchers with JourneyDataMongoHelper with AuthStub {
  lazy val testIncorrectAuthInternalId = "testIncorrectAuthInternalId"

  "GET /journey/:journeyId" when {
    "there is data stored against the journey ID" should {
      "return all data stored against the journey ID" in {
        val testData = Json.obj(
          "testField" -> "testValue"
        )

        insertById(testJourneyId, testInternalId, testData)

        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))

        val expectedData = Json.obj(
          "testField" -> "testValue",
          "authInternalId" -> testInternalId
        )

        val res = get(s"/journey/$testJourneyId")

        res.status mustBe OK
        res.json mustBe expectedData
      }
    }
    "there is no data stored against the journey ID" should {
      "return NOT_FOUND with a code explaining that no data can be found" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        val res = get(s"/journey/$testJourneyId")

        res.status mustBe NOT_FOUND
        res.json mustBe Json.obj(
          "code" -> "NOT_FOUND",
          "reason" -> s"No data exists for journey ID: $testJourneyId"
        )
      }
    }
    "the user cannot be authorised" should {
      "return Unauthorised" in {
        stubAuthFailure()

        val res = get(s"/journey/$testJourneyId")

        res.status mustBe UNAUTHORIZED
      }
    }
    "the provided internal ID does not match the ID on the record" should {
      "return Not Found" in {
        stubAuth(OK, successfulAuthResponse(Some(testIncorrectAuthInternalId)))

        val testData = Json.obj(
          "testField" -> "testValue"
        )
        insertById(testJourneyId, testInternalId, testData)

        val res = get(s"/journey/$testJourneyId")

        res.status mustBe NOT_FOUND
      }
    }
  }


  "GET /journey/:journeyId/:dataKey" when {
    "there is data stored against the journey ID containing the value in dataKey" should {
      "return all the data stored against the journeyId and dataKey" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        val testDataKey = "testDataKey"
        val testDataValue = "testDataValue"

        val testData = Json.obj(
          testDataKey -> testDataValue
        )
        insertById(testJourneyId, testInternalId, testData)

        val res = get(s"/journey/$testJourneyId/$testDataKey")

        res.status mustBe OK
        res.json mustBe JsString(testDataValue)
      }
    }

    "there is data stored against the journey ID but no data for the dataKey" should {
      "return NOT FOUND with a code indicating there is no data" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        val testDataKey = "testDataKey"

        insertById(testJourneyId, testInternalId)

        val res = get(s"/journey/$testJourneyId/$testDataKey")

        res.status mustBe NOT_FOUND
        res.json mustBe Json.obj(
          "code" -> "NOT_FOUND",
          "reason" -> s"No data exists for either journey ID: $testJourneyId or data key: $testDataKey"
        )
      }
    }

    "there is no data stored against the journey ID" should {
      "return all the data stored against the journeyId and dataKey" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        val testDataKey = "testDataKey"

        insertById(testJourneyId, testInternalId)

        val res = get(s"/journey/$testJourneyId/$testDataKey")

        res.status mustBe NOT_FOUND
        res.json mustBe Json.obj(
          "code" -> "NOT_FOUND",
          "reason" -> s"No data exists for either journey ID: $testJourneyId or data key: $testDataKey"
        )
      }
    }

    "the user cannot be authorised" should {
      "return Unauthorised" in {
        stubAuthFailure()

        val testDataKey = "testDataKey"

        val res = get(s"/journey/$testJourneyId/$testDataKey")

        res.status mustBe UNAUTHORIZED
      }
    }
    "the provided internal ID does not match the ID on the record" should {
      "return Not Found" in {
        stubAuth(OK, successfulAuthResponse(Some(testIncorrectAuthInternalId)))
        val testDataKey = "testDataKey"

        val res = get(s"/journey/$testJourneyId/$testDataKey")

        res.status mustBe NOT_FOUND
      }
    }

  }


}
