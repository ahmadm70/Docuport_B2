package app.docuport.step_definitions;

import app.docuport.pages.HomePage;
import app.docuport.pages.LoginPage;
import app.docuport.pages.ProfilePage;
import app.docuport.utilities.DocuportApiUtilities;
import app.docuport.utilities.Driver;
import app.docuport.utilities.Environment;
import io.cucumber.java.en.*;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static io.restassured.RestAssured.*;
import static org.junit.Assert.*;

public class LoginApiStepDefinitions {
    public static final Logger LOG = LogManager.getLogger();
    String baseUrl = Environment.BASE_URL;
    Response response;
    String accessToken;

    @Given("User logged in to Docuport api as advisor role")
    public void user_logged_in_to_Docuport_api_as_advisor_role() {
//        String email;
//        String password;
//        if (role.equals("advisor")) {
//             email = Environment.ADVISOR_EMAIL;
//             password = Environment.ADVISOR_PASSWORD;
//        } else if ( role.equals("employee")) {
//             email = Environment.EMPLOYEE_EMAIL;
//             password = Environment.EMPLOYEE_PASSWORD;
//        }
        String email = Environment.ADVISOR_EMAIL;
        String password = Environment.ADVISOR_PASSWORD;
        LOG.info("Authorizing advisor role: email: " + email, ", password: " + password);
        LOG.info(("Environment Base Url: " + baseUrl));
        accessToken = DocuportApiUtilities.getAccessToken(email, password);
        if (accessToken == null) {
            LOG.error("Could not authorize the user in server");
            // The one below is in JUnit as a method which also show error
            fail("Could not authorize the user in server");
        } else {
            LOG.info("Access token: " + accessToken);
        }
    }

    @Given("User sends GET request to {string} with query param {string} email address")
    public void user_sends_GET_request_to_with_query_param_email_address(String endpoint, String param) {
        String email = "";
//        if (param.equals("advisor")) {
//            email = Environment.ADVISOR_EMAIL;
//        } else {
//            email = null;
//        }
        switch (param) {
            case "advisor": // Advisor
            case "Advisor":
                email = Environment.ADVISOR_EMAIL;
                break;
            case "employee":
            case "Employee":
            case "EMPLOYEE":
                email = Environment.EMPLOYEE_EMAIL;
            default:
                LOG.info("Need to either implement the user type or that user type is invalid");
        }
        //This switch case can continue for all the roles
        response = given().accept(ContentType.JSON)
                .and().header("Authorization", accessToken)
                .and().queryParam("EmailAddress", email)
                .when().get(baseUrl + endpoint);


    }

    @Then("status code should be {int}")
    public void status_code_should_be(int expectedStatusCode) {
        assertEquals("Status code verification failed", expectedStatusCode, response.statusCode());
        response.then().statusCode(expectedStatusCode); // this is doing same as above
    }

    @Then("content type is {string}")
    public void content_type_is(String expContentType) {
        response.then().contentType(ContentType.JSON); // This is doing the same thing as the one below
        assertEquals("The content Type did not match ", expContentType, response.contentType());
    }

    @Then("role is {string}")
    public void role_is(String expRoleName) {
        assertEquals(expRoleName, response.path("items[0].roles[0].name"));
        JsonPath jsonPath = response.jsonPath();
        assertEquals(expRoleName, jsonPath.getString("items[0].roles[0].name"));
    }

    @Given("User logged in to Docuport app as advisor role")
    public void user_logged_in_to_Docuport_app_as_advisor_role() {
        Driver.getDriver().get(Environment.URL);
        LoginPage loginPage = new LoginPage();
        loginPage.login(Environment.ADVISOR_EMAIL, Environment.ADVISOR_PASSWORD);
        assertEquals("https://beta.docuport.app/", Driver.getDriver().getCurrentUrl());
    }

    @When("User goes to profile page")
    public void user_goes_to_profile_page() {
        HomePage homePage = new HomePage();
        homePage.goToProfilePage();
    }

    @Then("User should see same info on UI and API")
    public void user_should_see_same_info_on_UI_and_API() {
        ProfilePage profilePage = new ProfilePage();
        String fullName = profilePage.fullName.getText(); // Batch1 Group1
        String[] fN = fullName.split(" ");  // ["Batch1", "Group1"]
        String role = profilePage.role.getText();
        Map<String, String> uiUserDataMap = new HashMap<>();
        uiUserDataMap.put("role", role);
        uiUserDataMap.put("firstName", fN[0]);
        uiUserDataMap.put("lastName", fullName.split(" ")[1]);
        //uiUserDataMap.put("lastName", fN[1]);
        System.out.println("UI User Info: " + uiUserDataMap);
        String apiUserFirstName = response.path("items[0].firstName");
        String apiUserLastName = response.path("items[0].lastName");
        String apiUserRole = response.path("items[0].roles[0].displayName");
        Map<String, String> apiUserDataMap = new HashMap<>();
        apiUserDataMap.put("role", apiUserRole);
        apiUserDataMap.put("firstName", apiUserFirstName);
        apiUserDataMap.put("lastName", apiUserLastName);
        System.out.println("API User Info: " + apiUserDataMap);
        assertEquals(uiUserDataMap, apiUserDataMap);
    }
}