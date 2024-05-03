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
import play.api.libs.json.{JsObject, JsValue, Json}
import uk.gov.hmrc.soletraderidentification.models.{Address, FullName}
import utils.WireMockMethods

import java.time.LocalDate

trait CreateTemporaryReferenceNumberStub extends WireMockMethods {

  private val iFHeaders = Map(
    "Authorization" -> "Bearer dev",
    "Environment" -> "dev",
    "OriginatorId" -> "dev"
  )

  def stubCreateTemporaryReferenceNumber(birthDate: LocalDate,
                                         fullName: FullName,
                                         address: Address
                                        )(status: Int,
                                          body: JsValue = Json.obj()): StubMapping = {

    val jsonBody: JsObject =
      Json.obj(
        "birthDate" -> birthDate,
        "name" -> Json.obj(
          "forename" -> fullName.firstName,
          "surname" -> fullName.lastName
        ),
        "address" -> Json.toJson(address)
      )

    when(method = POST, uri = "/individuals/trn", body = jsonBody, headers = iFHeaders)
      .thenReturn(status, body)

  }
}
