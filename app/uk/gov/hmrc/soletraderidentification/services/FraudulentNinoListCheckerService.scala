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

import uk.gov.hmrc.soletraderidentification.config.AppConfig

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FraudulentNinoListCheckerService @Inject()(implicit ec: ExecutionContext, appConfig: AppConfig) {

  val cachedFraudulentNinos: Set[String] = appConfig.readFraudulentNinosFile

  def isAFraudulentNino(ninoToBeChecked: String): Future[Boolean] = Future {
    cachedFraudulentNinos.contains(ninoToBeChecked)
  }
}
