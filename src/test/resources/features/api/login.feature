Feature: Docuport API login verifications

  @regression
  Scenario: verify login with valid credentials
    Given User logged in to Docuport api as advisor role
    And User sends GET request to "/api/v1/identity/users" with query param advisor email address
    Then status code should be 200
    And content type is "application/json; charset=utf-8"
    And role is "advisor"