package com.ontestautomation.wiremock;

import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.jayway.restassured.RestAssured.*;

public class PingPongXmlTest {
	
	String okMessage = "<input>PING</input>";
	String faultMessage = "<input>PONG</input>";
		
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8090);
	
	@Test
	public void testPingPongPositive() {
		
		setupStub();
		
		given().
			body(okMessage).
		when().
			post("http://localhost:8090/pingpong").
		then().
			assertThat().
			statusCode(200).
			and().
			assertThat().body("output", org.hamcrest.Matchers.equalTo("PONG"));
	}
	
	@Test
	public void testPingPongNegative() {
		
		setupStub();
		
		given().
			body(faultMessage).
		when().
			post("http://localhost:8090/pingpong").
		then().
			assertThat().
			statusCode(404).
			and().
			assertThat().body("output", org.hamcrest.Matchers.not("PONG"));
	}
	
	public void setupStub() {
		
		stubFor(post(urlEqualTo("/pingpong"))
				.withRequestBody(matching(okMessage))
	            .willReturn(aResponse()
	                .withStatus(200)
	                .withHeader("Content-Type", "application/xml")
	                .withBody("<output>PONG</output>")));
	}
}