Feature: Testing Status Service

  Scenario: Get status of a transaction not storage in the system
    Given A transaction that is not stored in our system
    When I check the status from any channel
    Then The system returns the status 'INVALID'

  Scenario: Get status of a transaction with channel CLIENT And date before today
    Given A transaction that is stored in our system
    When I check the status from CLIENT channel
    And the transaction date is before today
    Then The system returns the status 'SETTLED'
    And the amount substracting the fee

  Scenario: Get status of a transaction with channel ATM And date before today
    Given A transaction that is stored in our system
    When I check the status from ATM channel
    And the transaction date is before today
    Then The system returns the status 'SETTLED'
    And the amount substracting the fee

  Scenario: Get status of a transaction with channel INTERNAL And date before today
    Given A transaction that is stored in our system
    When I check the status from INTERNAL channel
    And the transaction date is before today
    Then The system returns the status 'SETTLED'
    And the amount And the fee

  Scenario: Get status of a transaction with channel CLIENT And date equals today
    Given A transaction that is stored in our system
    When I check the status from CLIENT channel
    And the transaction date is equals to today
    Then The system returns the status 'PENDING'
    And the amount substracting the fee

  Scenario: Get status of a transaction with channel ATM And date equals today
    Given A transaction that is stored in our system
    When I check the status from ATM channel
    And the transaction date is equals to today
    Then The system returns the status 'PENDING'
    And the amount substracting the fee

  Scenario: Get status of a transaction with channel INTERNAL And date equals today
    Given A transaction that is stored in our system
    When I check the status from INTERNAL channel
    And the transaction date is equals to today
    Then The system returns the status 'PENDING'
    And the amount And the fee

  Scenario: Get status of a transaction with channel CLIENT And date after today
    Given A transaction that is stored in our system
    When I check the status from CLIENT channel
    And the transaction date is greater than today
    Then The system returns the status 'FUTURE'
    And the amount substracting the fee

  Scenario: Get status of a transaction with channel ATM And date after today
    Given A transaction that is stored in our system
    When I check the status from ATM channel
    And the transaction date is greater than today
    Then The system returns the status 'PENDING'
    And the amount substracting the fee

  Scenario: Get status of a transaction with channel INTERNAL and date after today
    Given A transaction that is stored in our system
    When I check the status from INTERNAL channel
    And the transaction date is greater than today
    Then The system returns the status 'FUTURE'
    And the amount And the fee
