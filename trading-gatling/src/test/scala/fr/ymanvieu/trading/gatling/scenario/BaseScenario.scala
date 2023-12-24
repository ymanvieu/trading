package fr.ymanvieu.trading.gatling.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder._

trait BaseScenario {

  val AUTHORIZATION_HEADER: Map[String, String] = Map("Authorization" -> "Bearer ${accessToken}")

  var username = "gatling"
  var password = "gatlinggatling"

  def login(): HttpRequestBuilder = {
    http(s"Login in as '$username'")
      .post("/api/auth")
      .body(StringBody(s"""{ "username": "$username", "password": "$password" }""")).asJson
      .check(status.is(200))
      .check(jsonPath("$.accessToken").exists.saveAs("accessToken"))
  }
}
