Feature: Chronicle
    In order montior and analyse an ActiveMQ broker
    As a system operator
    I want to get a list of events that have occured within the ActiveMQ broker

  Scenario: Capture send event
    When "hello world" is sent to the ActiveMQ queue "test.queue"
    Then the following event will be chronicled
      | event | desination         | msg         |
      | send  | queue://test.queue | hello world |
