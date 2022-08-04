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

import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import uk.gov.hmrc.soletraderidentification.featureswitch.core.models.FeatureSwitch

import javax.inject.Singleton

@Singleton
class FeatureSwitchingModule extends Module with FeatureSwitchRegistry {

  val switches = Seq(StubGetSaReference, DesStub, CreateTrnStub, InsightStub)

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(
      bind[FeatureSwitchRegistry].to(this).eagerly()
    )
  }
}

case object StubGetSaReference extends FeatureSwitch {
  override val configName: String = "feature-switch.sa-reference-stub"
  override val displayName: String = "Use stub for get SA Reference"
}

case object DesStub extends FeatureSwitch {
  override val configName: String = "feature-switch.des-stub"
  override val displayName: String = "Use stub for submissions to DES"
}

case object CreateTrnStub extends FeatureSwitch {
  override val configName: String = "feature-switch.trn-stub"
  override val displayName: String = "Use stub for generating temporary reference numbers"
}

case object InsightStub extends FeatureSwitch {
  override val configName: String = "feature-switch.insight-stub"
  override val displayName: String = "Use stub for insight"
}