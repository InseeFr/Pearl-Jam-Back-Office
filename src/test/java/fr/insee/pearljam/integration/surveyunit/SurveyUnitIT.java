package fr.insee.pearljam.integration.surveyunit;

import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.api.utils.ScriptConstants;
import fr.insee.pearljam.config.FixedDateServiceConfiguration;
import fr.insee.pearljam.domain.campaign.port.userside.DateService;
import fr.insee.pearljam.domain.exception.CommunicationTemplateNotFoundException;
import org.json.JSONException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(profiles = {"auth", "test"})
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(FixedDateServiceConfiguration.class)
@Transactional
class SurveyUnitIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private DateService dateService;

	@Test
	@DisplayName("Should return survey units associated to the interviewer")
	void testGetSurveyUnits() throws Exception {
		MvcResult mvcResult =
				mockMvc.perform(get("/api/survey-units").with(authentication(AuthenticatedUserTestHelper.AUTH_INTERVIEWER)).accept(MediaType.APPLICATION_JSON)).andReturn();

		String contentResult = mvcResult.getResponse().getContentAsString();
		String expectedResult = """
				    [
				       {
				          "id":"11",
				          "campaign":"SIMPSONS2020X00",
				          "campaignLabel":"Survey on the Simpsons tv show 2020",
				          "managementStartDate":1718966154301,
				          "interviewerStartDate":1719052554302,
				          "identificationPhaseStartDate":1719138954303,
				          "collectionStartDate":1719225354304,
				          "collectionEndDate":1721903754305,
				          "endDate":1724582154306,
				          "identificationConfiguration":"IASCO",
				          "contactOutcomeConfiguration":"F2F",
				          "contactAttemptConfiguration":"F2F",
				          "useLetterCommunication": true,
				          "collectNextContacts":false
				       },
				       {
				          "id":"12",
				          "campaign":"SIMPSONS2020X00",
				          "campaignLabel":"Survey on the Simpsons tv show 2020",
				          "managementStartDate":1718966154301,
				          "interviewerStartDate":1719052554302,
				          "identificationPhaseStartDate":1719138954303,
				          "collectionStartDate":1719225354304,
				          "collectionEndDate":1721903754305,
				          "endDate":1724582154306,
				          "identificationConfiguration":"IASCO",
				          "contactOutcomeConfiguration":"F2F",
				          "contactAttemptConfiguration":"F2F",
				          "useLetterCommunication": true,
				          "collectNextContacts":false
				       },
				       {
						 "id":"25",
						 "campaign":"SIMPSONS2020X00",
						 "campaignLabel":"Survey on the Simpsons tv show 2020",
						 "managementStartDate":1718966154308,
						 "interviewerStartDate":1719052554309,
						 "identificationPhaseStartDate":1719138954310,
						 "collectionStartDate":1719225354314,
						 "collectionEndDate":1721903754315,
						 "endDate":1724582154316,
						 "identificationConfiguration":"IASCO",
						 "contactOutcomeConfiguration":"F2F",
						 "contactAttemptConfiguration":"F2F",
						 "useLetterCommunication":false,
				          "collectNextContacts":false
					   },
				       {
				          "id":"20",
				          "campaign":"VQS2021X00",
				          "campaignLabel":"Everyday life and health survey 2021",
				          "managementStartDate":1718966154308,
				          "interviewerStartDate":1719052554308,
				          "identificationPhaseStartDate":1719138954308,
				          "collectionStartDate":1719225354308,
				          "collectionEndDate":1721903754308,
				          "endDate":1724582154308,
				          "identificationConfiguration":"IASCO",
				          "contactOutcomeConfiguration":"TEL",
				          "contactAttemptConfiguration":"TEL",
				          "useLetterCommunication": false,
				          "collectNextContacts":false
				       }
				    ]
				""";
		JSONAssert.assertEquals(expectedResult, contentResult, JSONCompareMode.NON_EXTENSIBLE);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}"
	 * return 200.
	 *
	 * @throws InterruptedException ie
	 * @throws JSONException        jse
	 */
	@Test
	void testGetSurveyUnitDetail() throws Exception {
		MvcResult result =
				mockMvc.perform(get("/api/interviewer/survey-unit/11").with(authentication(AuthenticatedUserTestHelper.AUTH_INTERVIEWER)).accept(MediaType.APPLICATION_JSON)).andExpectAll(status().isOk()).andReturn();

		String resultJson = result.getResponse().getContentAsString();
		String expectedJson = """
				{
				   "id":"11",
				   "displayName":"business-id-11",
				   "persons":[
				      {
				         "id":1,
				         "title":"MISTER",
				         "firstName":"Ted",
				         "lastName":"Farmer",
				         "email":"test@test.com",
				         "birthdate":11111111,
				         "privileged":true,
				         "phoneNumbers":[
				            {
				               "source":"FISCAL",
				               "favorite":false,
				               "number":"+33677542802"
				            },
				            {
				               "source":"FISCAL",
				               "favorite":true,
				               "number":"+33677542802"
				            }
				         ]
				      },
				      {
				         "id":6,
				         "title":"MISS",
				         "firstName":"Christine",
				         "lastName":"Aguilar",
				         "email":"test@test.com",
				         "birthdate":11111111,
				         "privileged":false,
				         "phoneNumbers":[
				            {
				               "source":"FISCAL",
				               "favorite":true,
				               "number":"+33677542802"
				            }
				         ]
				      },
				      {
				         "id":7,
				         "title":"MISS",
				         "firstName":"Louise",
				         "lastName":"Walker",
				         "email":"test@test.com",
				         "birthdate":11111111,
				         "privileged":false,
				         "phoneNumbers":[
				            {
				               "source":"FISCAL",
				               "favorite":true,
				               "number":"+33677542802"
				            }
				         ]
				      }
				   ],
				   "address":{
				      "l1":"Ted Farmer",
				      "l2":"",
				      "l3":"",
				      "l4":"1 rue de la gare",
				      "l5":"",
				      "l6":"29270 Carhaix",
				      "l7":"France",
				      "elevator":true,
				      "building":"Bat. C",
				      "floor":"Etg 4",
				      "door":"Porte 48",
				      "staircase":"Escalier B",
				      "cityPriorityDistrict":true
				   },
				   "priority":true,
				   "campaign":"SIMPSONS2020X00",
				   "comments":[],
				   "sampleIdentifiers":{
				      "bs":11,
				      "ec":"1",
				      "le":11,
				      "noi":11,
				      "numfa":11,
				      "rges":11,
				      "ssech":1,
				      "nolog":11,
				      "nole":11,
				      "autre":"11",
				      "nograp":"11"
				   },
				   "states":[
				      {
				         "id":1,
				         "date":111112111,
				         "type":"VIN"
				      },
				      {
				         "id":7,
				         "date":101111111,
				         "type":"TBR"
				      }
				   ],
				   "contactAttempts":[],
				   "identification":{
				      "identification":"IDENTIFIED",
				      "access":"ACC",
				      "situation":"ORDINARY",
				      "category":"PRIMARY",
				      "occupant":"IDENTIFIED"
				   },
				   "communicationTemplates":[
				      {
						 "id": "mesh1",
				      	 "campaignId": "SIMPSONS2020X00",
				         "meshuggahId":"mesh1",
				         "medium":"EMAIL",
				         "type":"REMINDER"
				      },
				      {
				      	 "id": "mesh2",
						 "campaignId": "SIMPSONS2020X00",
				         "meshuggahId":"mesh2",
				         "medium":"LETTER",
				         "type":"NOTICE"
				      }
				   ],
				   "communicationRequests":[
				      {
				         "communicationTemplateId":"mesh1",
				         "campaignId":"SIMPSONS2020X00",
				         "meshuggahId": "mesh1",
				         "reason":"REFUSAL",
				         "emitter":"INTERVIEWER",
				         "status":[
				            {
				               "date":1721903754305,
				               "status":"INITIATED"
				            },
				            {
				               "date":1721903755305,
				               "status":"READY"
				            },
				            {
				               "date":1721903756305,
				               "status":"SUBMITTED"
				            }
				         ]
				      },
				      {
				      	 "communicationTemplateId": "mesh2",
				         "campaignId":"SIMPSONS2020X00",
				         "meshuggahId": "mesh2",
				         "reason":"UNREACHABLE",
				         "emitter":"INTERVIEWER",
				         "status":[
				            {
				               "date":1721903754305,
				               "status":"INITIATED"
				            },
				            {
				               "date":1721903756310,
				               "status":"READY"
				            }
				         ]
				      }
				   ],
				   "previousContactHistory":
				     {
				       "comment":"nice comment",
				       "contactOutcomeValue":"INA",
				       "persons":[
				         {
				           "id":16,
				           "title":"MISS",
				           "firstName":"Opre",
				           "lastName":"Vious",
				           "panel":true,
				           "birthdate": 315532800000
				         },{
				           "id":17,
				           "title":"MISTER",
				           "firstName":"Agrippa",
				           "lastName":"Nel",
				           "panel":true,
				           "birthdate": 1024815788000
				         },{
				           "id":18,
				           "title":"MISTER",
				           "firstName":"Isidore",
				           "lastName":"Champ",
				           "panel":false,
				           "birthdate": 1070870588000
				         }
				        ]
				     },
				   "nextContactHistory":
				     {
				      "persons":[
				        {"id":15,
				         "title":"MISS",
				         "firstName":"Futur",
				         "lastName":"Ama",
				         "phoneNumber":"+33677542866",
				         "email":"futur.ama@ch.com",
				         "preferredContact": true
				         }
				        ]
				      }
				}""";
		JSONAssert.assertEquals(expectedJson, resultJson, JSONCompareMode.NON_EXTENSIBLE);
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}"
	 * return 200
	 *
	 * @throws Exception e
	 */
	@Test
	@Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
	void testPutSurveyUnitDetail() throws Exception {
		long currentTimestamp = dateService.getCurrentTimestamp();
		String updateJson = """
			{
			  "id": "20",
			  "persons": [
				{
				  "id": 10,
				  "title": "MISTER",
				  "firstName": "Harriette",
				  "lastName": "Raymond",
				  "email": "test@test.com",
				  "birthdate": 11111111,
				  "privileged": true,
				  "phoneNumbers": [
					{
					  "source": "FISCAL",
					  "favorite": true,
					  "number": "test"
					}
				  ]
				}
			  ],
			  "address": {
				"l1": "test1",
				"l2": "test2",
				"l3": "test3",
				"l4": "test4",
				"l5": "test5",
				"l6": "test6",
				"l7": "test7",
				"elevator": true,
				"building": "testBuilding",
				"floor": "testFloor",
				"door": "testDoor",
				"staircase": "testStaircase",
				"cityPriorityDistrict": true
			  },
			  "priority": false,
			  "campaign": "VQS2021X00",
			  "comments": [
				{
				  "type": "INTERVIEWER",
				  "value": "test-interviewer-comment"
				},
				{
				  "type": "MANAGEMENT",
				  "value": "test-management-comment"
				}
			  ],
			  "sampleIdentifiers": {
				"bs": 20,
				"ec": "2",
				"le": 20,
				"noi": 20,
				"numfa": 20,
				"rges": 20,
				"ssech": 1,
				"nolog": 20,
				"nole": 20,
				"autre": "20",
				"nograp": "20"
			  },
			  "states": [
				{
				  "date": 1590504459838,
				  "type": "AOC"
				},
				{
				  "id": 9,
				  "date": 1590504478334,
				  "type": "VIC"
				}
			  ],
			  "contactAttempts": [
				{
				  "date": 1589268626000,
				  "status": "NOC",
				  "medium": "TEL"
				},
				{
				  "date": 1589268800000,
				  "status": "INA",
				  "medium": "TEL"
				}
			  ],
			  "contactOutcome": {
				"date": 1589268626000,
				"type": "IMP",
				"totalNumberOfContactAttempts": 2
			  },
			  "communicationRequests": [
				{
				  "communicationTemplateId": "mesh3",
				  "reason": "REFUSAL",
				  "creationTimestamp": 1721903754305
				},
				{
				  "communicationTemplateId": "mesh4",
				  "reason": "UNREACHABLE",
				  "creationTimestamp": 1721903754405
				}
			  ],
			  "nextContactHistory":
				{
				  "persons": [
					{
					  "title": "MISS",
					  "firstName": "Futur",
					  "lastName": "Anna",
					  "phoneNumber":"+1234567890",
					  "email":"futur.ama@ch.upd",
					  "panel": true,
					  "preferredContact": true
					}
				  ]
				}
			}
			""";
		MvcResult result =
				mockMvc.perform(put("/api/survey-unit/20").with(authentication(AuthenticatedUserTestHelper.AUTH_INTERVIEWER)).accept(MediaType.APPLICATION_JSON).content(updateJson).contentType(MediaType.APPLICATION_JSON)).andReturn();

		String resultJson = result.getResponse().getContentAsString();
		String expectedJson = """
        {
          "id": "20",
          "persons": [
            {
              "id": 10,
              "title": "MISTER",
              "firstName": "Harriette",
              "lastName": "Raymond",
              "email": "test@test.com",
              "birthdate": 11111111,
              "privileged": true,
              "phoneNumbers": [
                {
                  "source": "FISCAL",
                  "favorite": true,
                  "number": "test"
                }
              ]
            }
          ],
          "address": {
            "l1": "test1",
            "l2": "test2",
            "l3": "test3",
            "l4": "test4",
            "l5": "test5",
            "l6": "test6",
            "l7": "test7",
            "elevator": true,
            "building": "testBuilding",
            "floor": "testFloor",
            "door": "testDoor",
            "staircase": "testStaircase",
            "cityPriorityDistrict": true
          },
          "priority": false,
          "campaign": "VQS2021X00",
          "comments": [
            {
              "type": "INTERVIEWER",
              "value": "test-interviewer-comment"
            },
            {
              "type": "MANAGEMENT",
              "value": "test-management-comment"
            }
          ],
          "sampleIdentifiers": {
            "bs": 20,
            "ec": "2",
            "le": 20,
            "noi": 20,
            "numfa": 20,
            "rges": 20,
            "ssech": 1,
            "nolog": 20,
            "nole": 20,
            "autre": "20",
            "nograp": "20"
          },
          "states": [
            {
              "id": 9,
              "date": 1590504478334,
              "type": "VIC"
            },
            {
              "id": 14,
              "date": 1590504459838,
              "type": "AOC"
            }
          ],
          "contactAttempts": [
            {
              "date": 1589268626000,
              "status": "NOC",
              "medium": "TEL"
            },
            {
              "date": 1589268800000,
              "status": "INA",
              "medium": "TEL"
            }
          ],
          "contactOutcome": {
            "date": 1589268626000,
            "type": "IMP",
            "totalNumberOfContactAttempts": 2
          },
          "communicationRequests": [
            {
              "communicationTemplateId": "mesh3",
              "campaignId": "VQS2021X00",
              "meshuggahId": "mesh3",
              "reason": "REFUSAL",
              "emitter": "INTERVIEWER",
              "status": [
                {
                  "date": 1721903754305,
                  "status": "INITIATED"
                },
                {
                  "date": """ + currentTimestamp + """
                  ,
                  "status": "READY"
                }
              ]
            },
            {
              "communicationTemplateId": "mesh4",
              "campaignId": "VQS2021X00",
              "meshuggahId": "mesh4",
              "reason": "UNREACHABLE",
              "emitter": "INTERVIEWER",
              "status": [
                {
                  "date": 1721903754205,
                  "status": "INITIATED"
                }
              ]
            },
            {
              "communicationTemplateId": "mesh4",
              "campaignId": "VQS2021X00",
              "meshuggahId": "mesh4",
              "reason": "UNREACHABLE",
              "emitter": "INTERVIEWER",
              "status": [
                {
                  "date": 1721903754405,
                  "status": "INITIATED"
                },
                {
                  "date": 1719324512000,
                  "status": "CANCELLED"
                }
              ]
            },
            {
              "communicationTemplateId": "mesh3",
              "campaignId": "VQS2021X00",
              "meshuggahId": "mesh3",
              "reason": "REFUSAL",
              "emitter": "INTERVIEWER",
              "status": [
                {
                  "date": 1721903754205,
                  "status": "INITIATED"
                }
              ]
            }
          ],
          "nextContactHistory":
            {
              "persons": [
                {
                  "id": 20,
                  "title": "MISS",
                  "firstName": "Futur",
                  "lastName": "Anna",
                  "phoneNumber": "+1234567890",
                  "email":"futur.ama@ch.upd",
                  "preferredContact": true
                }
              ]
            }
        }
        """;

		JSONAssert.assertEquals(expectedJson, resultJson, JSONCompareMode.NON_EXTENSIBLE);
	}

	@Test
	@DisplayName("Should throw exception when communication template not found")
	void testPutSurveyUnitDetailException() throws Exception {
		String updateJson = """
				{
				    "id":"20",
				    "persons":[
				       {
				          "id":10,
				          "title":"MISTER",
				          "firstName":"Harriette",
				          "lastName":"Raymond",
				          "email":"test@test.com",
				          "birthdate":11111111,
				          "privileged":true,
				          "phoneNumbers":[
				          ]
				       }
				    ],
				    "address":{
				       "l1":"test1",
				       "l2":"test2",
				       "l3":"test3",
				       "l4":"test4",
				       "l5":"test5",
				       "l6":"test6",
				       "l7":"test7",
				       "elevator":true,
				       "building":"testBuilding",
				       "floor":"testFloor",
				       "door":"testDoor",
				       "staircase":"testStaircase",
				       "cityPriorityDistrict":true
				    },
				    "priority":false,
				    "campaign":"VQS2021X00",
				    "comments":[],
				    "sampleIdentifiers":{
				       "bs":20,
				       "ec":"2",
				       "le":20,
				       "noi":20,
				       "numfa":20,
				       "rges":20,
				       "ssech":1,
				       "nolog":20,
				       "nole":20,
				       "autre":"20",
				       "nograp":"20"
				    },
				    "states":[
				       {
				          "date":1590504459838,
				          "type":"AOC"
				       }
				    ],
				    "contactAttempts":[],
				    "contactOutcome":{
				       "date":1589268626000,
				       "type":"IMP",
				       "totalNumberOfContactAttempts":2
				    },
				    "communicationRequests":[
				       {
				          "communicationTemplateId":"NOT_EXIST",
				          "reason":"UNREACHABLE",
				          "creationTimestamp": 1721903754405
				       }
				    ],
				    "contactHistory":[]
				 }""";
		String putUrl = "/api/survey-unit/20";
		mockMvc.perform(put(putUrl).with(authentication(AuthenticatedUserTestHelper.AUTH_INTERVIEWER)).accept(MediaType.APPLICATION_JSON).content(updateJson).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.NOT_FOUND, putUrl, CommunicationTemplateNotFoundException.MESSAGE));
	}

	@Test
	@DisplayName("Should return all survey-units for a campaign")
	void testGetAllSurveyUnitsByCampaign() throws Exception {
		MvcResult mvcResult =
				mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/survey-units").with(authentication(AuthenticatedUserTestHelper.AUTH_LOCAL_USER)).accept(MediaType.APPLICATION_JSON)).andReturn();

		String contentResult = mvcResult.getResponse().getContentAsString();
		String expectedResult = """
				[
				   {
				      "id":"12",
				      "displayName":"business-id-12",
				      "ssech":1,
				      "location":"90000",
				      "city":"Belfort",
				      "campaign":"Survey on the Simpsons tv show 2020",
				      "state":"TBR",
				      "reading":true,
				      "viewed":false,
				      "comments":[],
				      "interviewer":{
				         "id":"INTW1",
				         "interviewerFirstName":"Margie",
				         "interviewerLastName":"Lucas"
				      }
				   },
				   {
				      "id":"13",
				      "displayName":"business-id-13",
				      "ssech":2,
				      "location":"32230",
				      "city":"Marciac",
				      "campaign":"Survey on the Simpsons tv show 2020",
				      "state":"TBR",
				      "reading":true,
				      "viewed":false,
				      "comments":[
				         {
				            "type":"INTERVIEWER",
				            "value":"un commentaire"
				         }
				      ],
				      "interviewer":{
				         "id":"INTW2",
				         "interviewerFirstName":"Carlton",
				         "interviewerLastName":"Campbell"
				      }
				   },
				   {
				      "id":"24",
				      "displayName":"business-id-24",
				      "ssech":1,
				      "location":"35000",
				      "city":"Rennes",
				      "campaign":"Survey on the Simpsons tv show 2020",
				      "state":"TBR",
				      "reading":true,
				      "viewed":false,
				      "contactOutcome":{
				         "date":1590504478334,
				         "type":"DUK",
				         "totalNumberOfContactAttempts":null
				      },
				      "comments":[]
				   },
				   {
				      "id":"14",
				      "displayName":"business-id-14",
				      "ssech":3,
				      "location":"44190",
				      "city":"Clisson",
				      "campaign":"Survey on the Simpsons tv show 2020",
				      "state":"TBR",
				      "reading":true,
				      "viewed":false,
				      "comments":[],
				      "interviewer":{
				         "id":"INTW3",
				         "interviewerFirstName":"Gerald",
				         "interviewerLastName":"Edwards"
				      }
				   },
				   {
				      "id":"11",
				      "displayName":"business-id-11",
				      "ssech":1,
				      "location":"29270",
				      "city":"Carhaix",
				      "campaign":"Survey on the Simpsons tv show 2020",
				      "closingCause":"NPI",
				      "state":"VIN",
				      "reading":true,
				      "viewed":false,
				      "comments":[],
				      "interviewer":{
				         "id":"INTW1",
				         "interviewerFirstName":"Margie",
				         "interviewerLastName":"Lucas"
				      }
				   }
				]
				""";
		JSONAssert.assertEquals(expectedResult, contentResult, JSONCompareMode.NON_EXTENSIBLE);
	}

	@Test
	@DisplayName("Should return survey units for a campaign filtered by state")
	void testGetSurveyUnitsByCampaign02() throws Exception {
		MvcResult mvcResult =
				mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/survey-units?state=VIN").with(authentication(AuthenticatedUserTestHelper.AUTH_LOCAL_USER)).accept(MediaType.APPLICATION_JSON)).andReturn();

		String contentResult = mvcResult.getResponse().getContentAsString();
		String expectedResult = """
				[
				   {
				      "id":"11",
				      "displayName":"business-id-11",
				      "ssech":1,
				      "location":"29270",
				      "city":"Carhaix",
				      "campaign":"Survey on the Simpsons tv show 2020",
				      "closingCause":"NPI",
				      "state":"VIN",
				      "reading":true,
				      "viewed":false,
				      "comments":[],
				      "interviewer":{
				         "id":"INTW1",
				         "interviewerFirstName":"Margie",
				         "interviewerLastName":"Lucas"
				      }
				   }
				]
				""";
		JSONAssert.assertEquals(expectedResult, contentResult, JSONCompareMode.NON_EXTENSIBLE);
	}
}
