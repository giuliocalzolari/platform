package uk.ac.wellcome.platform.api.services

import com.sksamuel.elastic4s.ElasticDsl._
import org.junit.Test
import org.scalatest.{AsyncFunSpec, Matchers}
import uk.ac.wellcome.models.{IdentifiedWork, SourceIdentifier, Work}
import uk.ac.wellcome.platform.api.models.DisplayWork
import uk.ac.wellcome.test.utils.IndexedElasticSearchLocal
import uk.ac.wellcome.utils.JsonUtil

class ElasticSearchServiceTest
    extends AsyncFunSpec
    with IndexedElasticSearchLocal
    with Matchers {

  val elasticService =
    new ElasticSearchService(indexName, itemType, elasticClient)

  it("should return the records in Elasticsearch") {
    val firstIdentifiedWork =
      identifiedWorkWith(canonicalId = "1234",
                         label = "this is the first item label")
    val secondIdentifiedWork =
      identifiedWorkWith(canonicalId = "5678",
                         label = "this is the second item label")
    insertIntoElasticSearch(firstIdentifiedWork, secondIdentifiedWork)

    val displayWorksFuture = elasticService.findWork()

    displayWorksFuture map { displayWork =>
      displayWork should have size 2
      displayWork.head shouldBe DisplayWork("Work",
                                   firstIdentifiedWork.canonicalId,
                                   firstIdentifiedWork.work.label)
      displayWork.tail.head shouldBe DisplayWork("Work",
                                        secondIdentifiedWork.canonicalId,
                                        secondIdentifiedWork.work.label)
    }
  }

  it("should get a DisplayWork by id") {
    insertIntoElasticSearch(
      identifiedWorkWith(canonicalId = "1234",
                         label = "this is the item label"))

    val recordsFuture = elasticService.findWorkById("1234")

    recordsFuture map { records =>
      records.isDefined shouldBe true
      records.get shouldBe DisplayWork("Work",
                                  "1234",
                                  "this is the item label",
                                  None)
    }
  }

  it("should return a future of None if it cannot get arecord by id") {
    val recordsFuture = elasticService.findWorkById("1234")

    recordsFuture map { record =>
      record shouldBe None
    }
  }

  private def insertIntoElasticSearch(identifiedWorks: IdentifiedWork*) = {
    identifiedWorks.foreach { identifiedWork =>
      elasticClient.execute(
        indexInto(indexName / itemType)
          .id(identifiedWork.canonicalId)
          .doc(JsonUtil.toJson(identifiedWork).get))
    }
    eventually {
      elasticClient
        .execute {
          search(indexName).matchAllQuery()
        }
        .await
        .hits should have size identifiedWorks.size
    }
  }

  private def identifiedWorkWith(canonicalId: String, label: String) = {
    IdentifiedWork(canonicalId,
                   Work(identifiers = List(
                          SourceIdentifier(source = "Calm",
                                           sourceId = "AltRefNo",
                                           value = "calmid")),
                        label = label))
  }
}
