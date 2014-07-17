Feature: Home feature
  
  Scenario: As a registered user on the device I should see the menu
    Given I am logged into the application
    Then I should see "Dispense"
    Then I should see "Receive"
    Then I should see "Order"
    Then I should see "Losses"
    Then I should see "Reports"
    Then I should see "Messages"
    Then I should see "Help"