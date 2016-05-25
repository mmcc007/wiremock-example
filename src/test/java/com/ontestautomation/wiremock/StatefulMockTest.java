package com.ontestautomation.wiremock;

import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.Scenario;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.jayway.restassured.RestAssured.*;

public class StatefulMockTest {
		
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8090);
	
	@Test
	public void testStatefulMock() {
		
		setupStub();
		
		given().
		when().
			get("http://localhost:8090/todolist").
		then().
			assertThat().
			statusCode(200).
			and().
			assertThat().body("list", org.hamcrest.Matchers.equalTo("Empty"));
		
		given().
		when().
			post("http://localhost:8090/todolist").
		then().
			assertThat().
			statusCode(201);
		
		given().
		when().
			get("http://localhost:8090/todolist").
		then().
			assertThat().
			statusCode(200).
			and().
			assertThat().body("list", org.hamcrest.Matchers.not("Empty")).
			and().
			assertThat().body("list.item", org.hamcrest.Matchers.equalTo("Item added to list"));
		
	}
	
	public void setupStub() {
		
		stubFor(get(urlEqualTo("/todolist"))
				.inScenario("addItem")
				.whenScenarioStateIs(Scenario.STARTED)
				.willReturn(aResponse()
	                .withStatus(200)
	                .withHeader("Content-Type", "application/xml")
	                .withBody("<list>Empty</list>")));
		
		stubFor(post(urlEqualTo("/todolist"))
				.inScenario("addItem")
				.whenScenarioStateIs(Scenario.STARTED)
				.willSetStateTo("itemAdded")
				.willReturn(aResponse()
					.withHeader("Content-Type", "application/xml")
	                .withStatus(201)));
		
		stubFor(get(urlEqualTo("/todolist"))
				.inScenario("addItem")
				.whenScenarioStateIs("itemAdded")
				.willReturn(aResponse()
	                .withStatus(200)
	                .withHeader("Content-Type", "application/xml")
	                .withBody("<list><item>Item added to list</item></list>")));
		
	}
}