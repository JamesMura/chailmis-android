Feature: Register feature
  
  Scenario: As a valid user I can register on the device
    When the application starts
    Then I see "DHIS LMIS"
    When I enter text "android_1" into field with id "textUsername"
    And I enter text "Password1" into field with id "textPassword"
    And I press the "Register" button
    Then I should see "Registering"

  Scenario: As a wrong user I can not register on the device
    When the application starts
    Then I see "DHIS LMIS"
    When I enter text "some username" into field with id "textUsername"
    And I enter text "some wrong password" into field with id "textPassword"
    And I press the "Register" button
    Then I should see "Invalid Username or Password"

  Scenario: As a wrong username or password shows error
    When the application starts
    Then I see "DHIS LMIS"
    When I enter text "" into field with id "textUsername"
    And I enter text "some wrong password" into field with id "textPassword"
    And I press the "Register" button
    Then I should see "Username is required"
    When I enter text "some username" into field with id "textUsername"
    And I enter text "" into field with id "textPassword"
    And I press the "Register" button
    Then I should see "Password is required"
