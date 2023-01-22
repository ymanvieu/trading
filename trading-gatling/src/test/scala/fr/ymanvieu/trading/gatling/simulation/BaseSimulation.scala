package fr.ymanvieu.trading.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BaseSimulation extends Simulation {

  val httpProtocol = http.baseUrl("http://localhost:8000")
}
