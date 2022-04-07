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

package uk.gov.hmrc.soletraderidentification.repositories

import play.api.Logger
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.commands.UpdateWriteResult
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.JsObjectDocumentWriter
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.soletraderidentification.config.AppConfig
import uk.gov.hmrc.soletraderidentification.models.JourneyDataModel
import uk.gov.hmrc.soletraderidentification.repositories.JourneyDataRepository._

import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class JourneyDataRepository @Inject()(reactiveMongoComponent: ReactiveMongoComponent,
                                      appConfig: AppConfig
                                     )(implicit ec: ExecutionContext) extends ReactiveRepository(
  collectionName = "sole-trader-identification",
  mongo = reactiveMongoComponent.mongoConnector.db,
  domainFormat = JourneyDataModel.MongoFormat,
  idFormat = implicitly[Format[String]]
) {

  def createJourney(journeyId: String, authInternalId: String): Future[String] =
    collection.insert(true).one(
      Json.obj(
        JourneyIdKey -> journeyId,
        AuthInternalIdKey -> authInternalId,
        CreationTimestampKey -> Json.obj("$date" -> Instant.now.toEpochMilli)
      )
    ).map(_ => journeyId)

  def getJourneyData(journeyId: String, authInternalId: String): Future[Option[JsObject]] =
    collection.find(
      Json.obj(
        JourneyIdKey -> journeyId,
        AuthInternalIdKey -> authInternalId
      ),
      Some(Json.obj(
        JourneyIdKey -> 0
      ))
    ).one[JsObject]

  def updateJourneyData(journeyId: String, dataKey: String, data: JsValue, authInternalId: String): Future[UpdateWriteResult] =
    collection.update(true).one(
      Json.obj(
        JourneyIdKey -> journeyId,
        AuthInternalIdKey -> authInternalId
      ),
      Json.obj(
        "$set" -> Json.obj(dataKey -> data)
      ),
      upsert = false,
      multi = false
    ).filter(_.n == 1)

  def removeJourneyDataField(journeyId: String, authInternalId: String, dataKey: String): Future[UpdateWriteResult] =
    collection.update(true).one(
      Json.obj(
        JourneyIdKey -> journeyId,
        AuthInternalIdKey -> authInternalId
      ),
      Json.obj(
        "$unset" -> Json.obj(dataKey -> 1)
      ),
      upsert = false,
      multi = false
    ).filter(_.n == 1)

  def removeJourneyData(journeyId: String, authInternalId: String): Future[UpdateWriteResult] =
    collection.update(true).one(
      Json.obj(
        JourneyIdKey -> journeyId,
        AuthInternalIdKey -> authInternalId
      ),
      Json.obj(
        JourneyIdKey -> journeyId,
        AuthInternalIdKey -> authInternalId,
        CreationTimestampKey -> Json.obj("$date" -> Instant.now.toEpochMilli)
      ),
      upsert = false,
      multi = false
    ).filter(_.n == 1)

  private val TtlIndexName = "SoleTraderDataExpires"

  private lazy val ttlIndex = Index(
    Seq(("creationTimestamp", IndexType.Ascending)),
    name = Some(TtlIndexName),
    options = BSONDocument("expireAfterSeconds" -> appConfig.timeToLiveSeconds)
  )

  private def setIndex(): Unit = {
    collection.indexesManager.drop(TtlIndexName) onComplete {
      _ => collection.indexesManager.ensure(ttlIndex)
    }
  }

  setIndex()

  override def drop(implicit ec: ExecutionContext): Future[Boolean] =
    collection.drop(failIfNotFound = false).map { r =>
      setIndex()
      r
    }

  def runOnce = {
    collection.count(
      Some(Json.obj(CreationTimestampKey -> Json.obj("$exists" -> false))),
      0,
      0,
      None
    )
  }

  runOnce.map(count => logger.warn("Number of documents that have no creation timestamp: " + count))
}

object JourneyDataRepository {
  val JourneyIdKey: String = "_id"
  val AuthInternalIdKey: String = "authInternalId"
  val CreationTimestampKey: String = "creationTimestamp"
}


