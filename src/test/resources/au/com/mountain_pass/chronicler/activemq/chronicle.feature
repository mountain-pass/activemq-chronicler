Feature: Chronicle
    In order montior and analyse an ActiveMQ broker
    As a system operator
    I want to get a list of events that have occured within the ActiveMQ broker

  Scenario: Capture send event
    Given "TheClient" is listenting for messages on the ActiveMQ queue "test.queue"
    When "TheProducer" sends "hello world" to the ActiveMQ queue "test.queue"
    Then the following event will be chronicled
      | event | client      | desination         | msg         |
      | send  | TheProducer | queue://test.queue | hello world |
    And the following event will be chronicled
      | event | client      | desination         | msg         |
      | recv  | TheClient   | queue://test.queue | hello world |
      