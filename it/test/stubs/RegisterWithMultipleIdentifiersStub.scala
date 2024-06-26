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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.{JsObject, Json}
import utils.WireMockMethods

trait RegisterWithMultipleIdentifiersStub extends WireMockMethods {

  private def registerResponseSuccessBody(safeId: String): JsObject =
    Json.obj(
      "identification" -> Json.arr(
        Json.obj(
          "idType" -> "SAFEID",
          "idValue" -> safeId
        )
      )
    )

  val registerResponseFailureBody: JsObject =
    Json.obj(
      "code" -> "INVALID_PAYLOAD",
      "reason" -> "Request has not passed validation. Invalid payload."
    )

  val registerResponseMultipleFailureBody: JsObject =
    Json.obj(
      "failures" -> Json.arr(
        Json.obj(
          "code" -> "INVALID_PAYLOAD",
          "reason" -> "Request has not passed validation. Invalid payload."
        ),
        Json.obj(
          "code" -> "INVALID_REGIME",
          "reason" -> "Request has not passed validation.  Invalid regime."
        )
      )
    )


  def stubRegisterWithNinoSuccess(nino: String, sautr: String, regime: String)(status: Int, safeId: String): StubMapping = {
    val postBody = Json.obj("soleTrader" ->
      Json.obj("nino" -> nino,
        "sautr" -> sautr
      )
    )
    when(method = POST, uri = s"/cross-regime/register/GRS\\?grsRegime=$regime", postBody)
      .thenReturn(
        status = status,
        body = registerResponseSuccessBody(safeId)
      )
  }

  def stubRegisterWithNinoNoSautrSuccess(nino: String, regime: String)(status: Int, safeId: String): StubMapping = {
    val postBody = Json.obj("soleTrader" ->
      Json.obj(
        "nino" -> nino
      )
    )
    when(method = POST, uri = s"/cross-regime/register/GRS\\?grsRegime=$regime", postBody)
      .thenReturn(
        status = status,
        body = registerResponseSuccessBody(safeId)
      )
  }

  def stubRegisterWithNinoFailure(nino: String, sautr: String, regime: String)(status: Int, body: JsObject): StubMapping = {
    val postBody = Json.obj("soleTrader" ->
      Json.obj("nino" -> nino,
        "sautr" -> sautr
      ))
    when(method = POST, uri = s"/cross-regime/register/GRS\\?grsRegime=$regime", postBody)
      .thenReturn(
        status = status,
        body = body
      )
  }

  def stubRegisterWithNinoNoSautrFailure(nino: String, regime: String)(status: Int): StubMapping = {
    val postBody = Json.obj("soleTrader" ->
      Json.obj("nino" -> nino))

    when(method = POST, uri = s"/cross-regime/register/GRS\\?grsRegime=$regime", postBody)
      .thenReturn(
        status = status,
        body = registerResponseFailureBody
      )
  }

  def stubRegisterWithTrnSuccess(trn: String, sautr: String, regime: String)(status: Int, safeId: String): StubMapping = {
    val postBody = Json.obj("soleTrader" ->
      Json.obj("tempNI" -> trn,
        "sautr" -> sautr
      )
    )
    when(method = POST, uri = s"/cross-regime/register/GRS\\?grsRegime=$regime", postBody)
      .thenReturn(
        status = status,
        body = registerResponseSuccessBody(safeId)
      )
  }

  def stubRegisterWithTrnFailure(trn: String, sautr: String, regime: String)(status: Int): StubMapping = {
    val postBody = Json.obj("soleTrader" ->
      Json.obj("tempNI" -> trn,
        "sautr" -> sautr
      ))
    when(method = POST, uri = s"/cross-regime/register/GRS\\?grsRegime=$regime", postBody)
      .thenReturn(
        status = status,
        body = registerResponseFailureBody
      )
  }

}
