package fr.ymanvieu.trading.gatling.simulation

import fr.ymanvieu.trading.gatling.scenario.RateHistoryScenario
import io.gatling.core.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps

class RateHistorySimulation extends BaseSimulation {

  val scn = RateHistoryScenario.buildScenario()

  setUp(scn.inject(rampUsers(2).during(5 seconds), constantUsersPerSec(2).during(30 seconds)).protocols(httpProtocol))
}