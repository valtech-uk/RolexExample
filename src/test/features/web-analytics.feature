Feature: web-analytics verification

  Scenario: 1.Verify WebAnalytics available on Home page
    Given I start UI test
    When I open Rolex page
    Then Web Analytics is available in source code
    And Assets Javascript is loaded
    And Smetrics request was sent with all correct values


  Scenario: 2.Verify WebAnalytics available on Watches page
    Given I start UI test and go to Watches page
    Then Web Analytics is available in source code
    And Assets Javascript is loaded
    And Smetrics request was sent with all correct values

  Scenario: 3.Verify WebAnalytics available on Model page
    Given I start UI test and go to Model page
    Then Web Analytics is available in source code
    And Assets Javascript is loaded
    And Smetrics request was sent with all correct values


  Scenario: 4.Verify WebAnalytics available on Model page
    Given I start UI test and go to Model page
    When I add the watch to the Wishlist
    Then Web Analytics is available in source code
    And Assets Javascript is loaded
    And Smetrics request was sent with all correct values