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

package uk.gov.hmrc.soletraderidentification.featureswitch.core.config

import uk.gov.hmrc.soletraderidentification.featureswitch.core.models.FeatureSwitch

trait FeatureSwitchRegistry {

  def switches: Seq[FeatureSwitch]

  def apply(name: String): FeatureSwitch =
    get(name) match {
      case Some(switch) => switch
      case None => throw new IllegalArgumentException("Invalid feature switch: " + name)
    }

  def get(name: String): Option[FeatureSwitch] = switches find (_.configName == name)

}
