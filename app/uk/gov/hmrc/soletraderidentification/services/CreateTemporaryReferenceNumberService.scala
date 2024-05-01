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


import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.soletraderidentification.connectors.CreateTemporaryReferenceNumberConnector
import uk.gov.hmrc.soletraderidentification.models.{Address, FullName}

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class CreateTemporaryReferenceNumberService @Inject()(createTemporaryReferenceConnector: CreateTemporaryReferenceNumberConnector) {

  def createTemporaryReferenceNumber(dateOfBirth: LocalDate,
                                     fullName: FullName,
                                     address: Address
                                    )(implicit hc: HeaderCarrier): Future[String] =
    createTemporaryReferenceConnector.createTemporaryReferenceNumber(dateOfBirth, fullName, address)

}
