package app.docuport.step_definitions;

import app.docuport.utilities.ConfigurationReader;
import io.cucumber.java.en.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.*;

public class HelloWorldApiStepDefinitions {
    public static final Logger LOG = LogManager.getLogger();
    Response response;

    @Given("User sends get request to hello world api")
    public void user_sends_get_request_to_hello_world_api() {
        LOG.info("Sending GET request to Hello World API...");
        response = given().accept(ContentType.JSON)
                .when().get(ConfigurationReader.getProperty("hello.world.api"));
    }

    @Then("hello world api status code is {int}")
    public void hello_world_api_status_code_is(int expectedStatusCode) {
        assertEquals(response.statusCode(), expectedStatusCode);
    }

    @Then("hello world api response body contains {string}")
    public void hello_world_api_response_body_contains(String expectedBodyText) {
        assertEquals(response.path("message"), expectedBodyText);
    }
}