package fr.ymanvieu.trading.gatling.scenario

import io.gatling.core.Predef._
import io.gatling.core.structure._
import io.gatling.http.Predef._

import scala.language.postfixOps

object HomeScenario extends BaseScenario {

  def buildScenario(): ScenarioBuilder = {
    scenario("Login")
      .exec(login())
      .exitHereIfFailed
      .group("Requests after login") {
        exec(http("Get Latest rates")
          .get("/api/rate/latest")
          .headers(AUTHORIZATION_HEADER)
          .check(status.is(200)))
        .exec(http("Get Portofolio")
          .get("/api/portofolio")
          .headers(AUTHORIZATION_HEADER)
          .check(status.is(200)))
        .exec(http("Get available symbols")
          .get("/api/portofolio/available-symbols")
          .headers(AUTHORIZATION_HEADER)
          .check(status.is(200)))
      }
  }
}
