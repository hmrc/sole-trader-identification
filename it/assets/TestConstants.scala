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

package assets


import uk.gov.hmrc.soletraderidentification.models.{Address, FullName}

import java.time.LocalDate
import java.util.UUID

object TestConstants {

  val testJourneyId: String = UUID.randomUUID().toString
  val testInternalId: String = UUID.randomUUID().toString
  val testSafeId: String = UUID.randomUUID().toString
  val testNino: String = "AA111111A"
  val testSautr: String = "1234567890"
  val testTrn: String = "1234567890"

  val testDateOfBirth: LocalDate = LocalDate.of(2021,8,13)
  val testFullName: FullName = FullName("fore","sur")
  val testAddress: Address = Address("line1", "line2", Some("line3"), Some("line4"), Some("line5"), "AA11 11A", "GB")


}
