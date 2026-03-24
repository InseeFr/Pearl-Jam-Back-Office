Feature: Identification

  Scenario Outline: Configure a campaign identification behavior
    Given a user with "admin" role
    When the user create a campaign with identificationConfiguration equals to "<identificationConfiguration>"
    Then the created campaign should have the identification configuration "<identificationConfiguration>"

    Examples:
      | identificationConfiguration |
      | IASCO                       |
      | HOUSEF2F                    |
      | HOUSETEL                    |
      | HOUSETELWSR                 |
      | INDF2F                      |
      | INDF2FNOR                   |
      | INDTEL                      |
      | INDTELNOR                   |
      | SRCVREINT                   |
      | NOIDENT                     |


  Scenario Outline: Persist a survey unit identification
    Given a user with "interviewer" role
    And a survey-unit is in a campaign with identification configuration equals to "<identificationConfiguration>"
    And this survey-unit is affected to this interviewer
    When the interviewer update the survey-unit with identification value
    Then the survey-unit is updated and its identification equals:
    """
    <expectedValue>
    """

    Examples:
      | identificationConfiguration | expectedValue                                                                                                                                                     |
      | IASCO                       | {"identification":"UNIDENTIFIED","access":"ACC","situation":"ORDINARY","category":"PRIMARY","occupant":"IDENTIFIED"}                                              |
      | HOUSEF2F                    | {"identification":"UNIDENTIFIED","access":"ACC","situation":"ORDINARY","category":"PRIMARY","occupant":"IDENTIFIED"}                                              |
      | HOUSETEL                    | {"situation":"ORDINARY","category":"PRIMARY"}                                                                                                                     |
      | HOUSETELWSR                 | {"situation":"ORDINARY","category":"PRIMARY"}                                                                                                                     |
      | INDF2F                      | {"individualStatus":"SAME_ADDRESS","interviewerCanProcess":"YES","situation":"ORDINARY"}                                                                          |
      | INDF2FNOR                   | {"individualStatus":"SAME_ADDRESS","interviewerCanProcess":"YES","situation":"ORDINARY"}                                                                          |
      | INDTEL                      | {"individualStatus":"SAME_ADDRESS","situation":"ORDINARY"}                                                                                                        |
      | INDTELNOR                   | {"individualStatus":"SAME_ADDRESS","situation":"ORDINARY"}                                                                                                        |
      | SRCVREINT                   | {"numberOfRespondents":"ONE","individualStatus":"SAME_ADDRESS","householdComposition":"SAME_COMPO","presentInPreviousHome":"AT_LEAST_ONE","situation":"ORDINARY"} |
