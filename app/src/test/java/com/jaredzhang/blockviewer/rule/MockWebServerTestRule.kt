package com.jaredzhang.blockviewer.rule

import okhttp3.Headers
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockWebServerTestRule : TestRule {

  val server: MockWebServer
    get() = statement.server

  val requestCount: Int
    get() = server.requestCount

  val baseUrl: String
    get() = server.url("/").toString()

  private lateinit var statement: MockWebServerStatement

  override fun apply(base: Statement, description: Description?): Statement {
    statement = MockWebServerStatement(base)
    return statement
  }

  private class MockWebServerStatement(private val base: Statement) : Statement() {

    val server = MockWebServer()

    override fun evaluate() {
      server.start()
      try {
        base.evaluate()
      } finally {
        server.shutdown()
      }
    }
  }


  fun respondWith(mockResponse: MockResponse): MockWebServerTestRule {
    server.enqueue(mockResponse)
    return this
  }

  fun respondWithError(responseCode: Int, body: String? = null, headers: Headers? = null): MockWebServerTestRule {
    val response = MockResponse().setResponseCode(responseCode)
    body?.let { response.setBody(it) }
    headers?.let { response.setHeaders(headers) }
    return respondWith(response)
  }

}
