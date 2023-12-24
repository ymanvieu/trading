package fr.ymanvieu.trading.gatling.simulation

import fr.ymanvieu.trading.gatling.scenario.RateHistoryScenario
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._
import scala.language.postfixOps

class RateHistorySimulation extends BaseSimulation {

  val scn: ScenarioBuilder = RateHistoryScenario.buildScenario()

  // open model
  setUp(scn.inject(rampUsers(2).during(5 seconds), constantUsersPerSec(1).during(30 seconds)).protocols(httpProtocol))
}
