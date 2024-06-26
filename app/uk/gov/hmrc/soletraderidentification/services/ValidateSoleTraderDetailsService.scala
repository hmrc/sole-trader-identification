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
import uk.gov.hmrc.soletraderidentification.connectors.GetSaReferenceConnector
import uk.gov.hmrc.soletraderidentification.models.{DetailsMatched, DetailsMismatched, DetailsNotFound, SoleTraderDetailsValidationResult}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ValidateSoleTraderDetailsService @Inject()(getSaReferenceConnector: GetSaReferenceConnector)(implicit ec: ExecutionContext) {

  def validateDetails(companyNumber: String, ctutr: String)(implicit hc: HeaderCarrier): Future[SoleTraderDetailsValidationResult] = {
    getSaReferenceConnector.getSaReference(companyNumber).map {
      case Some(retrievedCtutr) if retrievedCtutr == ctutr =>
        DetailsMatched
      case Some(_) =>
        DetailsMismatched
      case None =>
        DetailsNotFound
    }
  }

}
