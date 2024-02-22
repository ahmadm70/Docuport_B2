package app.docuport.step_definitions;

import app.docuport.utilities.DocuportApiUtilities;
import app.docuport.utilities.Environment;
import io.cucumber.java.en.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static org.junit.Assert.*;

public class LoginApiStepDefinitions {
    String baseURL = Environment.BASE_URL;
    Response response;
    String token;
    String email;
    String password;

    @Given("User logged in to Docuport api as advisor role")
    public void user_logged_in_to_Docuport_api_as_advisor_role() {
        email = Environment.ADVISOR_EMAIL;
        password = Environment.ADVISOR_PASSWORD;
        token = DocuportApiUtilities.getAccessToken(email, password);
    }

    @Given("User sends GET request to {string} with query param advisor email address")
    public void user_sends_GET_request_to_with_query_param_advisor_email_address(String endPoint) {
        response = given().accept(ContentType.JSON)
                .and().header("Authorization", token)
                .queryParams("emailAddress", email)
                .when().get(baseURL + endPoint);
    }

    @Then("status code should be {int}")
    public void status_code_should_be(int statusCode) {
        assertEquals(response.statusCode(), statusCode);
    }

    @Then("content type is {string}")
    public void content_type_is(String contentType) {
        assertEquals(response.contentType(), contentType);
    }

    @Then("role is {string}")
    public void role_is(String role) {
        assertEquals(response.path("items[0].roles[0].name"), role);
    }
}