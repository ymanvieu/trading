package fr.ymanvieu.trading.gatling.scenario

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilderBase
import io.gatling.core.structure._
import io.gatling.http.Predef._

object RateHistoryScenario {

  val feeder: FeederBuilderBase[String] = Array(
    Map("fromcur" -> "USD", "tocur" -> "EUR"),
    Map("fromcur" -> "BTC", "tocur" -> "USD"),
    Map("fromcur" -> "ETH", "tocur" -> "USD")
  ).circular


  def buildScenario(): ScenarioBuilder = {
    scenario("Rate History")
      .feed(feeder)
      .exec(http("Get ${fromcur}/${tocur} rate history")
        .get("/api/rate/history")
        .queryParam("fromcur", "${fromcur}")
        .queryParam("tocur", "${tocur}")
        .check(status.is(200)))
  }
}
