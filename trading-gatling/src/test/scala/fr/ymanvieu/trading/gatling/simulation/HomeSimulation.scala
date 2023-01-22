package fr.ymanvieu.trading.gatling.simulation

import fr.ymanvieu.trading.gatling.scenario._
import io.gatling.core.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps

class HomeSimulation extends BaseSimulation {

  val scn = HomeScenario.buildScenario()

  setUp(scn.inject(rampUsers(2).during(5 seconds), constantUsersPerSec(5).during(30 seconds)).protocols(httpProtocol))
}