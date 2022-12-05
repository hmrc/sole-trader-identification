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

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class FeatureSwitchingModuleSpec extends AnyWordSpec with Matchers {

  object TestFeatureSwitchingModule extends FeatureSwitchingModule()

  "FeatureSwitchingModule" should {

    "contain the feature switches StubGetSaReference, DesStub, CreateTrnStub and InsightStub" in {

      TestFeatureSwitchingModule.switches mustBe Seq(StubGetSaReference, DesStub, CreateTrnStub, InsightStub)

    }

    "be able to access the feature switch StubGetSaReference" in {

      TestFeatureSwitchingModule("feature-switch.sa-reference-stub") mustBe StubGetSaReference

    }

    "be able to access the feature switch DesStub" in {

      TestFeatureSwitchingModule("feature-switch.des-stub") mustBe DesStub

    }

    "be able to access the feature switch CreateTrnStub" in {

      TestFeatureSwitchingModule("feature-switch.trn-stub") mustBe CreateTrnStub

    }

    "be able to access the feature switch InsightStub" in {

      TestFeatureSwitchingModule("feature-switch.insight-stub") mustBe InsightStub

    }

    "raise an IllegalArgumentException when asked to access an unknown feature switch" in {

      try {
        TestFeatureSwitchingModule("unknown")
        fail("FeatureSwitchingModule must throw an IllegalArgumentException if the feature switch is not known")
      } catch {
        case _: IllegalArgumentException => succeed
      }

    }

  }

}
