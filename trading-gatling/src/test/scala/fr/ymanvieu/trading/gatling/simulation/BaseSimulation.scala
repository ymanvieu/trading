package fr.ymanvieu.trading.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class BaseSimulation extends Simulation {

  val httpProtocol: HttpProtocolBuilder = http.baseUrl("http://localhost:8080")
}
