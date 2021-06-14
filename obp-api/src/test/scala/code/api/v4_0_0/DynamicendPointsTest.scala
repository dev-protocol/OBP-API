package code.api.v4_0_0

import code.api.ResourceDocs1_4_0.SwaggerDefinitionsJSON
import code.api.util.APIUtil.OAuth._
import code.api.util.ApiRole._
import code.api.util.ErrorMessages.{DynamicEndpointExists, EndpointMappingNotFoundByOperationId, InvalidMyDynamicEndpointUser, UserHasMissingRoles, UserNotLoggedIn}
import code.api.util.ExampleValue
import code.api.v4_0_0.OBPAPI4_0_0.Implementations4_0_0
import code.entitlement.Entitlement
import com.github.dwickern.macros.NameOf.nameOf
import com.openbankproject.commons.model.ErrorMessage
import com.openbankproject.commons.util.ApiVersion
import net.liftweb.json.JArray
import net.liftweb.json.JsonAST.{JField, JString}
import net.liftweb.json.Serialization.write
import org.scalatest.Tag

class DynamicEndpointsTest extends V400ServerSetup {
  /**
    * Test tags
    * Example: To run tests with tag "getPermissions":
    * 	mvn test -D tagsToInclude
    *
    *  This is made possible by the scalatest maven plugin
    */
  object VersionOfApi extends Tag(ApiVersion.v4_0_0.toString)
  object ApiEndpoint1 extends Tag(nameOf(Implementations4_0_0.createDynamicEndpoint))
  object ApiEndpoint2 extends Tag(nameOf(Implementations4_0_0.getDynamicEndpoints))
  object ApiEndpoint3 extends Tag(nameOf(Implementations4_0_0.getDynamicEndpoint))
  object ApiEndpoint4 extends Tag(nameOf(Implementations4_0_0.deleteDynamicEndpoint))
  object ApiEndpoint5 extends Tag(nameOf(Implementations4_0_0.getMyDynamicEndpoints))
  object ApiEndpoint6 extends Tag(nameOf(Implementations4_0_0.deleteMyDynamicEndpoint))
  object ApiEndpoint7 extends Tag(nameOf(Implementations4_0_0.updateDynamicEndpointHost))
  object ApiEndpoint8 extends Tag(nameOf(Implementations4_0_0.dynamicEndpoint))
  

  feature(s"test $ApiEndpoint1 version $VersionOfApi - Unauthorized access") {
    scenario("We will call the endpoint without user credentials", ApiEndpoint1, VersionOfApi) {
      val postDynamicEndpointRequestBodyExample = ExampleValue.dynamicEndpointRequestBodyExample

      When("We make a request v4.0.0")
      val request400 = (v4_0_0_Request / "management" / "dynamic-endpoints").POST
      val response400 = makePostRequest(request400, write(postDynamicEndpointRequestBodyExample))
      Then("We should get a 401")
      response400.code should equal(401)
      response400.body.extract[ErrorMessage].message should equal(UserNotLoggedIn)
    }
  }

  feature(s"test $ApiEndpoint1 version $VersionOfApi - authorized access- missing role") {
    scenario("We will call the endpoint with user credentials", ApiEndpoint1, VersionOfApi) {
      val postDynamicEndpointRequestBodyExample = ExampleValue.dynamicEndpointRequestBodyExample

      When("We make a request v4.0.0")
      val request = (v4_0_0_Request / "management" / "dynamic-endpoints").POST<@ (user1)
      val response = makePostRequest(request, write(postDynamicEndpointRequestBodyExample))
      Then("We should get a 403")
      response.code should equal(403)
      response.body.extract[ErrorMessage].message.toString contains (UserHasMissingRoles) should be (true)
    }
  }

  feature(s"test $ApiEndpoint1 version $VersionOfApi - authorized access - with role - should be success!") {
    scenario("We will call the endpoint with user credentials", ApiEndpoint1, VersionOfApi) {
      When("We make a request v4.0.0")
      val postDynamicEndpointRequestBodyExample = ExampleValue.dynamicEndpointRequestBodyExample

      When("We make a request v4.0.0")
      val request = (v4_0_0_Request / "management" / "dynamic-endpoints").POST<@ (user1)
      val response = makePostRequest(request, write(postDynamicEndpointRequestBodyExample))
      Then("We should get a 403")
      response.code should equal(403)
      response.body.extract[ErrorMessage].message.toString contains (UserHasMissingRoles) should be (true)

      Then("We grant the role to the user1")
      Entitlement.entitlement.vend.addEntitlement("", resourceUser1.userId, canCreateDynamicEndpoint.toString)

      val responseWithRole = makePostRequest(request, write(postDynamicEndpointRequestBodyExample))
      Then("We should get a 201")
      responseWithRole.code should equal(201)
      responseWithRole.body.toString contains("dynamic_endpoint_id") should be (true)
      responseWithRole.body.toString contains("swagger_string") should be (true)
      responseWithRole.body.toString contains("Swagger Petstore") should be (true)
      responseWithRole.body.toString contains("This is a sample server Petstore server.") should be (true)
      responseWithRole.body.toString contains("apiteam@swagger.io") should be (true)
    }
  }

  feature(s"test $ApiEndpoint2 version $VersionOfApi - Unauthorized access") {
    scenario("We will call the endpoint without user credentials", ApiEndpoint2, VersionOfApi) {
      When("We make a request v4.0.0")
      val request400 = (v4_0_0_Request / "management" / "dynamic-endpoints").GET
      val response400 = makeGetRequest(request400)
      Then("We should get a 401")
      response400.code should equal(401)
      response400.body.extract[ErrorMessage].message should equal(UserNotLoggedIn)
    }
  }

  feature(s"test $ApiEndpoint2 version $VersionOfApi - authorized access- missing role") {
    scenario("We will call the endpoint with user credentials", ApiEndpoint2, VersionOfApi) {
      When("We make a request v4.0.0")
      val request = (v4_0_0_Request / "management" / "dynamic-endpoints").GET<@ (user1)
      val response = makeGetRequest(request)
      Then("We should get a 400")
      response.code should equal(403)
      response.body.extract[ErrorMessage].message.toString contains (UserHasMissingRoles) should be (true)
    }
  }

  feature(s"test $ApiEndpoint2 version $VersionOfApi - authorized access - with role - should be success!") {
    scenario("We will call the endpoint with user credentials", ApiEndpoint2, VersionOfApi) {
      When("We make a request v4.0.0")
      val postDynamicEndpointRequestBodyExample = ExampleValue.dynamicEndpointRequestBodyExample

      When("We make a request v4.0.0")
      val request = (v4_0_0_Request / "management" / "dynamic-endpoints").POST<@ (user1)
      val response = makePostRequest(request, write(postDynamicEndpointRequestBodyExample))
      Then("We should get a 403")
      response.code should equal(403)
      response.body.extract[ErrorMessage].message.toString contains (UserHasMissingRoles) should be (true)

      Then("We grant the role to the user1")
      Entitlement.entitlement.vend.addEntitlement("", resourceUser1.userId, CanGetDynamicEndpoints.toString)
      Entitlement.entitlement.vend.addEntitlement("", resourceUser1.userId, CanCreateDynamicEndpoint.toString)


      val newSwagger = postDynamicEndpointRequestBodyExample.transformField {
        case JField(name, value) if name.startsWith("/") => JField(s"$name/abc", value)
      }

      val responseWithRole = makePostRequest(request, write(newSwagger))
      Then("We should get a 201")
      responseWithRole.code should equal(201)


      val duplicatedRequest = makePostRequest(request, write(newSwagger))
      Then("We should get a 400")
      duplicatedRequest.code should equal(400)
      duplicatedRequest.body.extract[ErrorMessage].message.toString contains (DynamicEndpointExists) should be (true)


      val request400 = (v4_0_0_Request / "management" / "dynamic-endpoints").GET<@ (user1)
      val response400 = makeGetRequest(request400)
      response400.code should be (200)

      response400.body.toString contains("Swagger Petstore") should be (true)
      response400.body.toString contains("This is a sample server Petstore server.") should be (true)
      response400.body.toString contains("apiteam@swagger.io") should be (true)

    }
  }

  feature(s"test $ApiEndpoint3 version $VersionOfApi - Unauthorized access") {
    scenario("We will call the endpoint without user credentials", ApiEndpoint3, VersionOfApi) {
      When("We make a request v4.0.0")
      val request400 = (v4_0_0_Request / "management" / "dynamic-endpoints"/ "some-id").GET
      val response400 = makeGetRequest(request400)
      Then("We should get a 401")
      response400.code should equal(401)
      response400.body.extract[ErrorMessage].message should equal(UserNotLoggedIn)
    }
  }

  feature(s"test $ApiEndpoint3 version $VersionOfApi - authorized access- missing role") {
    scenario("We will call the endpoint with user credentials", ApiEndpoint3, VersionOfApi) {
      When("We make a request v4.0.0")
      val request = (v4_0_0_Request / "management" / "dynamic-endpoints" /"some-id").GET<@ (user1)
      val response = makeGetRequest(request)
      Then("We should get a 400")
      response.code should equal(403)
      response.body.extract[ErrorMessage].message.toString contains (UserHasMissingRoles) should be (true)
    }
  }

  feature(s"test $ApiEndpoint3 version $VersionOfApi - authorized access - with role - should be success!") {
    scenario("We will call the endpoint with user credentials", ApiEndpoint3, VersionOfApi) {
      When("We make a request v4.0.0")
      val postDynamicEndpointRequestBodyExample = ExampleValue.dynamicEndpointRequestBodyExample

      When("We make a request v4.0.0")
      val request = (v4_0_0_Request / "management" / "dynamic-endpoints").POST<@ (user1)
      val response = makePostRequest(request, write(postDynamicEndpointRequestBodyExample))
      Then("We should get a 403")
      response.code should equal(403)
      response.body.extract[ErrorMessage].message.toString contains (UserHasMissingRoles) should be (true)

      Then("We grant the role to the user1")
      Entitlement.entitlement.vend.addEntitlement("", resourceUser1.userId, CanGetDynamicEndpoint.toString)
      Entitlement.entitlement.vend.addEntitlement("", resourceUser1.userId, CanCreateDynamicEndpoint.toString)

      val newSwagger = postDynamicEndpointRequestBodyExample.transformField {
        case JField(name, value) if name.startsWith("/") => JField(s"$name/def", value)
      }

      val responseWithRole = makePostRequest(request, write(newSwagger))
      Then("We should get a 201")
      responseWithRole.code should equal(201)


      val duplicatedRequest = makePostRequest(request, write(newSwagger))
      Then("We should get a 400")
      duplicatedRequest.code should equal(400)
      duplicatedRequest.body.extract[ErrorMessage].message.toString contains (DynamicEndpointExists) should be (true)


      val id = responseWithRole.body.\\("dynamic_endpoint_id").values.get("dynamic_endpoint_id").head.toString

      val request400 = (v4_0_0_Request / "management" / "dynamic-endpoints" /id).GET<@ (user1)
      val response400 = makeGetRequest(request400)
      response400.code should be (200)
      response400.body.toString contains("dynamic_endpoint_id") should be (true)
      response400.body.toString contains("swagger_string") should be (true)
      response400.body.toString contains("Swagger Petstore") should be (true)
      response400.body.toString contains("This is a sample server Petstore server.") should be (true)
      response400.body.toString contains("apiteam@swagger.io") should be (true)

    }
  }

  feature(s"test $ApiEndpoint4 version $VersionOfApi - Unauthorized access") {
    scenario("We will call the endpoint without user credentials", ApiEndpoint4, VersionOfApi) {
      When("We make a request v4.0.0")
      val request400 = (v4_0_0_Request / "management" / "dynamic-endpoints"/ "some-id").DELETE
      val response400 = makeDeleteRequest(request400)
      Then("We should get a 401")
      response400.code should equal(401)
      response400.body.extract[ErrorMessage].message should equal(UserNotLoggedIn)
    }
  }

  feature(s"test $ApiEndpoint4 version $VersionOfApi - authorized access- missing role") {
    scenario("We will call the endpoint with user credentials", ApiEndpoint4, VersionOfApi) {
      When("We make a request v4.0.0")
      val request = (v4_0_0_Request / "management" / "dynamic-endpoints" /"some-id").DELETE<@ (user1)
      val response = makeDeleteRequest(request)
      Then("We should get a 400")
      response.code should equal(403)
      response.body.extract[ErrorMessage].message.toString contains (UserHasMissingRoles) should be (true)
    }
  }

  feature(s"test $ApiEndpoint4 version $VersionOfApi - authorized access - with role - should be success!") {
    scenario("We will call the endpoint with user credentials", ApiEndpoint4, VersionOfApi) {
      When("We make a request v4.0.0")
      val postDynamicEndpointRequestBodyExample = ExampleValue.dynamicEndpointRequestBodyExample

      When("We make a request v4.0.0")
      val request = (v4_0_0_Request / "management" / "dynamic-endpoints").POST<@ (user1)
      val response = makePostRequest(request, write(postDynamicEndpointRequestBodyExample))
      Then("We should get a 403")
      response.code should equal(403)
      response.body.extract[ErrorMessage].message.toString contains (UserHasMissingRoles) should be (true)

      Then("We grant the role to the user1")
      Entitlement.entitlement.vend.addEntitlement("", resourceUser1.userId, CanGetDynamicEndpoint.toString)
      Entitlement.entitlement.vend.addEntitlement("", resourceUser1.userId, CanCreateDynamicEndpoint.toString)
      Entitlement.entitlement.vend.addEntitlement("", resourceUser1.userId, CanDeleteDynamicEndpoint.toString)

      val newSwagger = postDynamicEndpointRequestBodyExample.transformField {
        case JField(name, value) if name.startsWith("/") => JField(s"$name/def2", value)
      }

      val responseWithRole = makePostRequest(request, write(newSwagger))
      Then("We should get a 201")
      responseWithRole.code should equal(201)


      val id = responseWithRole.body.\\("dynamic_endpoint_id").values.get("dynamic_endpoint_id").head.toString

      val request400 = (v4_0_0_Request / "management" / "dynamic-endpoints" /id).GET<@ (user1)
      val response400 = makeGetRequest(request400)
      response400.code should be (200)
      response400.body.toString contains("dynamic_endpoint_id") should be (true)
      response400.body.toString contains("swagger_string") should be (true)
      response400.body.toString contains("Swagger Petstore") should be (true)
      response400.body.toString contains("This is a sample server Petstore server.") should be (true)
      response400.body.toString contains("apiteam@swagger.io") should be (true)


      val requestDelete = (v4_0_0_Request / "management" / "dynamic-endpoints" /id).DELETE<@ (user1)
      val responseDelete = makeDeleteRequest(requestDelete)
      responseDelete.code should be (204)

      val responseGetAgain = makeGetRequest(request400)
      responseGetAgain.code should be (404)


    }
  }

  feature(s"test $ApiEndpoint5 and $ApiEndpoint6 version $VersionOfApi - authorized access - should be success!") {
    scenario("We will call the endpoint with user credentials", ApiEndpoint5, ApiEndpoint6, VersionOfApi) {
      When("We make a request v4.0.0")
      val postDynamicEndpointRequestBodyExample = ExampleValue.dynamicEndpointRequestBodyExample

      val request = (v4_0_0_Request / "management" / "dynamic-endpoints").POST<@ (user1)

      Then("We grant the role to the user1")
      Entitlement.entitlement.vend.addEntitlement("", resourceUser1.userId, CanCreateDynamicEndpoint.toString)
      val newSwagger = postDynamicEndpointRequestBodyExample.transformField {
        case JField(name, value) if name.startsWith("/") => JField(s"$name/def2", value)
      }

      val responseWithRole = makePostRequest(request, write(newSwagger))
      Then("We should get a 201")
      responseWithRole.code should equal(201)


      val id = responseWithRole.body.\\("dynamic_endpoint_id").values.get("dynamic_endpoint_id").head.toString

      val request400 = (v4_0_0_Request / "my" / "dynamic-endpoints").GET<@ (user1)
      val response400 = makeGetRequest(request400)
      response400.code should be (200)
      response400.body.toString contains("dynamic_endpoint_id") should be (true)
      response400.body.toString contains("swagger_string") should be (true)
      response400.body.toString contains("Swagger Petstore") should be (true)
      response400.body.toString contains("This is a sample server Petstore server.") should be (true)
      response400.body.toString contains("apiteam@swagger.io") should be (true)

      {
        // we use the wrong user2 to get the dynamic-endpoints
        val request400 = (v4_0_0_Request / "my" / "dynamic-endpoints").GET<@ (user2)
        val response400 = makeGetRequest(request400)
        Then("We should get a 200")
        response400.code should equal(200)
        val json = response400.body \ "dynamic_endpoints"
        val dynamicEntitiesGetJson = json.asInstanceOf[JArray]
        dynamicEntitiesGetJson.values should have size 0

      }

      {
        val requestDelete = (v4_0_0_Request / "my" / "dynamic-endpoints" /id).DELETE<@ (user2)
        val responseDelete = makeDeleteRequest(requestDelete)
        Then("We should get a 400")
        responseDelete.code should equal(400)
        responseDelete.body.extract[ErrorMessage].message should startWith (InvalidMyDynamicEndpointUser)
      }
      val requestDelete = (v4_0_0_Request / "my" / "dynamic-endpoints" /id).DELETE<@ (user1)
      val responseDelete = makeDeleteRequest(requestDelete)
      responseDelete.code should be (204)

      val responseDeleteAgain = makeDeleteRequest(requestDelete)
      responseDeleteAgain.code should be (404)
    }
  }

  feature(s"test $ApiEndpoint7 version $VersionOfApi - - Unauthorized access") {
    scenario("We will call the endpoint with user credentials", ApiEndpoint7, VersionOfApi) {
      When("We make a request v4.0.0")
      val postDynamicEndpointRequestBodyExample = ExampleValue.dynamicEndpointRequestBodyExample

      When("We make a request v4.0.0")
      val request = (v4_0_0_Request / "management" / "dynamic-endpoints").POST<@ (user1)
      val response = makePostRequest(request, write(postDynamicEndpointRequestBodyExample))
      Then("We should get a 403")
      response.code should equal(403)
      response.body.extract[ErrorMessage].message.toString contains (UserHasMissingRoles) should be (true)

      Then("We grant the role to the user1")
      Entitlement.entitlement.vend.addEntitlement("", resourceUser1.userId, canCreateDynamicEndpoint.toString)

      val responseWithRole = makePostRequest(request, write(postDynamicEndpointRequestBodyExample))
      Then("We should get a 201")
      responseWithRole.code should equal(201)
      responseWithRole.body.toString contains("dynamic_endpoint_id") should be (true)
      val dynamicEndpointId = (responseWithRole.body \"dynamic_endpoint_id").asInstanceOf[JString].s

      Then("We update the host")
      val dynamicEndpointHostJson = SwaggerDefinitionsJSON.dynamicEndpointHostJson400

      When("We make a request v4.0.0")
      val requestPut = (v4_0_0_Request / "management" / "dynamic-endpoints"/dynamicEndpointId/ "host").PUT
      val responsePut = makePutRequest(requestPut, write(postDynamicEndpointRequestBodyExample))
      Then("We should get a 401")
      responsePut.code should equal(401)
      responsePut.body.extract[ErrorMessage].message should equal(UserNotLoggedIn)
    }
  }


  feature(s"test $ApiEndpoint7 version $VersionOfApi - authorized access - missing role!") {
    scenario("We will call the endpoint with user credentials", ApiEndpoint7, VersionOfApi) {
      When("We make a request v4.0.0")
      val postDynamicEndpointRequestBodyExample = ExampleValue.dynamicEndpointRequestBodyExample

      When("We make a request v4.0.0")
      val request = (v4_0_0_Request / "management" / "dynamic-endpoints").POST<@ (user1)
      val response = makePostRequest(request, write(postDynamicEndpointRequestBodyExample))
      Then("We should get a 403")
      response.code should equal(403)
      response.body.extract[ErrorMessage].message.toString contains (UserHasMissingRoles) should be (true)

      Then("We grant the role to the user1")
      Entitlement.entitlement.vend.addEntitlement("", resourceUser1.userId, canCreateDynamicEndpoint.toString)

      val responseWithRole = makePostRequest(request, write(postDynamicEndpointRequestBodyExample))
      Then("We should get a 201")
      responseWithRole.code should equal(201)
      responseWithRole.body.toString contains("dynamic_endpoint_id") should be (true)
      val dynamicEndpointId = (responseWithRole.body \"dynamic_endpoint_id").asInstanceOf[JString].s

      Then("We update the host")
      val dynamicEndpointHostJson = SwaggerDefinitionsJSON.dynamicEndpointHostJson400

      When("We make a request v4.0.0")
      val requestPut = (v4_0_0_Request / "management" / "dynamic-endpoints"/dynamicEndpointId/ "host").PUT<@ (user1)
      val responsePut = makePutRequest(requestPut, write(postDynamicEndpointRequestBodyExample))
      Then("We should get a 403")
      responsePut.code should equal(403)
      responsePut.body.extract[ErrorMessage].message.toString contains (UserHasMissingRoles) should be (true)
      responsePut.body.extract[ErrorMessage].message.toString contains (CanUpdateDynamicEndpoint.toString()) should be (true)
    }
  }

  feature(s"test $ApiEndpoint7 version $VersionOfApi - authorized access - with role - should be success!") {
    scenario("We will call the endpoint with user credentials", ApiEndpoint7, VersionOfApi) {
      When("We make a request v4.0.0")
      val postDynamicEndpointRequestBodyExample = ExampleValue.dynamicEndpointRequestBodyExample

      When("We make a request v4.0.0")
      val request = (v4_0_0_Request / "management" / "dynamic-endpoints").POST<@ (user1)
      val response = makePostRequest(request, write(postDynamicEndpointRequestBodyExample))
      Then("We should get a 403")
      response.code should equal(403)
      response.body.extract[ErrorMessage].message.toString contains (UserHasMissingRoles) should be (true)

      Then("We grant the role to the user1")
      Entitlement.entitlement.vend.addEntitlement("", resourceUser1.userId, canCreateDynamicEndpoint.toString)

      val responseWithRole = makePostRequest(request, write(postDynamicEndpointRequestBodyExample))
      Then("We should get a 201")
      responseWithRole.code should equal(201)
      responseWithRole.body.toString contains("dynamic_endpoint_id") should be (true)
      val dynamicEndpointId = (responseWithRole.body \"dynamic_endpoint_id").asInstanceOf[JString].s

      Then("We update the host")
      val dynamicEndpointHostJson = SwaggerDefinitionsJSON.dynamicEndpointHostJson400

      When("We make a request v4.0.0")
      val requestPut = (v4_0_0_Request / "management" / "dynamic-endpoints"/dynamicEndpointId/ "host").PUT<@ (user1)
      val responsePut = makePutRequest(requestPut, write(postDynamicEndpointRequestBodyExample))
      Then("We should get a 403")
      response.code should equal(403)
      response.body.extract[ErrorMessage].message.toString contains (UserHasMissingRoles) should be (true)

      Then("We grant the role to the user1")
      Entitlement.entitlement.vend.addEntitlement("", resourceUser1.userId, canUpdateDynamicEndpoint.toString)

      When("We make a request v4.0.0")
      val responseWithRolePut = makePutRequest(requestPut, write(dynamicEndpointHostJson))
      Then("We should get a 201")
      responseWithRolePut.code should equal(201)
      (responseWithRolePut.body \ "host").asInstanceOf[JString].s shouldEqual (dynamicEndpointHostJson.host)


    }
  }

  feature(s"test $ApiEndpoint1 and $ApiEndpoint8 version $VersionOfApi - authorized access - with role - should be success!") {
    scenario("we test new endpoints - system level", ApiEndpoint8, VersionOfApi) {
      When("We make a request v4.0.0")
      val dynamicEndpointSwagger = """{
                                     |  "swagger": "2.0",
                                     |  "info": {
                                     |    "title": "Documents",
                                     |    "version": "1.0.0"
                                     |  },
                                     |  "definitions": {
                                     |    "FashionBrandNames": {
                                     |      "type": "object",
                                     |      "properties": {
                                     |        "name": {
                                     |          "type": "string"
                                     |        }
                                     |      }
                                     |    }
                                     |  },
                                     |  "paths": {
                                     |    "/documents": {
                                     |      "post": {
                                     |        "operationId": "POST_documents",
                                     |        "produces": [
                                     |          "application/json"
                                     |        ],
                                     |        "responses": {
                                     |          "201": {
                                     |            "description": "Success Response",
                                     |            "schema": {
                                     |              "$ref": "#/definitions/FashionBrandNames"
                                     |            }
                                     |          }
                                     |        },
                                     |        "consumes": [
                                     |          "application/json"
                                     |        ],
                                     |        "description": "POST Documents",
                                     |        "summary": "POST Documents"
                                     |      }
                                     |    }
                                     |  },
                                     |  "host": "obp_mock"
                                     |}""".stripMargin
      Entitlement.entitlement.vend.addEntitlement("", resourceUser1.userId, canCreateDynamicEndpoint.toString)
      val request = (v4_0_0_Request / "management" / "dynamic-endpoints").POST<@ (user1)
      val responseWithRole = makePostRequest(request, dynamicEndpointSwagger)
      Then("We should get a 201")
      responseWithRole.code should equal(201)
      val dynamicEndpointId = (responseWithRole.body \"dynamic_endpoint_id").asInstanceOf[JString].s
      
      Then("we test authentication error")
      
      {
        val request = (v4_0_0_Request / "dynamic" / "documents").POST
        val response = makePostRequest(request, dynamicEndpointSwagger)
        response.code should equal(401)
        response.body.toString contains(UserNotLoggedIn) should be (true)
      }

      Then("we test missing role error")
      
      {
        val request = (v4_0_0_Request / "dynamic" / "documents").POST<@ (user2)
        val response = makePostRequest(request, dynamicEndpointSwagger)
        response.code should equal(403)
        response.body.toString contains(UserHasMissingRoles) should be (true)
      }

      Then("we test successful cases")
      
      {
        val request = (v4_0_0_Request / "dynamic" / "documents").POST<@ (user1)
        val response = makePostRequest(request, dynamicEndpointSwagger)
        response.code should equal(201)
        response.body.toString contains("name") should be (true)
        response.body.toString contains("String") should be (true)
      }


      Then(s"we test $ApiEndpoint7, if we change the host, then the response is different")
      
      {
        val dynamicEndpointHostJson = SwaggerDefinitionsJSON.dynamicEndpointHostJson400
        Then("We grant the role to the user1 and update the host")
        Entitlement.entitlement.vend.addEntitlement("", resourceUser1.userId, canUpdateDynamicEndpoint.toString)
        val requestPut = (v4_0_0_Request / "management" / "dynamic-endpoints"/dynamicEndpointId/ "host").PUT<@ (user1)
        val responsePut = makePutRequest(requestPut, write(dynamicEndpointHostJson))
        
        Then("if we changed the host, the response should be the errors")
        val request = (v4_0_0_Request / "dynamic" / "documents").POST<@ (user1)
        val response = makePostRequest(request, dynamicEndpointSwagger)
        response.code should equal(404)
        response.body.toString contains(EndpointMappingNotFoundByOperationId) should be (true)
      }

    }

    scenario("we test new endpoints - bank level", ApiEndpoint8, VersionOfApi) {
      When("We make a request v4.0.0 with the role canCreateDynamicEndpoint")
      val dynamicEndpointSwagger = """{
                                     |  "swagger": "2.0",
                                     |  "info": {
                                     |    "title": "Documents",
                                     |    "version": "1.0.0"
                                     |  },
                                     |  "definitions": {
                                     |    "FashionBrandNames": {
                                     |      "type": "object",
                                     |      "properties": {
                                     |        "name": {
                                     |          "type": "string"
                                     |        }
                                     |      }
                                     |    }
                                     |  },
                                     |  "paths": {
                                     |    "/documents": {
                                     |      "post": {
                                     |        "operationId": "POST_documents",
                                     |        "produces": [
                                     |          "application/json"
                                     |        ],
                                     |        "responses": {
                                     |          "201": {
                                     |            "description": "Success Response",
                                     |            "schema": {
                                     |              "$ref": "#/definitions/FashionBrandNames"
                                     |            }
                                     |          }
                                     |        },
                                     |        "consumes": [
                                     |          "application/json"
                                     |        ],
                                     |        "description": "POST Documents",
                                     |        "summary": "POST Documents"
                                     |      }
                                     |    }
                                     |  },
                                     |  "host": "obp_mock"
                                     |}""".stripMargin
      
      Then("First test the bank Level role")
      
      {
        Entitlement.entitlement.vend.addEntitlement("", resourceUser1.userId, canCreateBankLevelDynamicEndpoint.toString)
        val request = (v4_0_0_Request / "management" /"banks"/testBankId1.value/ "dynamic-endpoints").POST<@ (user1)
        val responseWithRole = makePostRequest(request, dynamicEndpointSwagger)
        Then("We should get a 201")
        responseWithRole.code should equal(201)
        val dynamicEndpointId = (responseWithRole.body \"dynamic_endpoint_id").asInstanceOf[JString].s
      }

      Then("First test the system Level role")
      Entitlement.entitlement.vend.addEntitlement("", resourceUser1.userId, canCreateDynamicEndpoint.toString)
      val request = (v4_0_0_Request / "management" /"banks"/testBankId1.value/ "dynamic-endpoints").POST<@ (user1)
      val responseWithRole = makePostRequest(request, dynamicEndpointSwagger)
      Then("We should get a 201")
      responseWithRole.code should equal(201)
      val dynamicEndpointId = (responseWithRole.body \"dynamic_endpoint_id").asInstanceOf[JString].s
      
      Then("we test authentication error")

      {
        val request = (v4_0_0_Request / "dynamic" / "documents").POST
        val response = makePostRequest(request, dynamicEndpointSwagger)
        response.code should equal(401)
        response.body.toString contains(UserNotLoggedIn) should be (true)
      }

      Then("we test missing role error")

      {
        val request = (v4_0_0_Request / "dynamic" / "documents").POST<@ (user2)
        val response = makePostRequest(request, dynamicEndpointSwagger)
        response.code should equal(403)
        response.body.toString contains(UserHasMissingRoles) should be (true)
      }

      Then("we test successful cases")

      {
        val request = (v4_0_0_Request / "dynamic" / "documents").POST<@ (user1)
        val response = makePostRequest(request, dynamicEndpointSwagger)
        response.code should equal(201)
        response.body.toString contains("name") should be (true)
        response.body.toString contains("String") should be (true)
      }


      Then(s"we test $ApiEndpoint7, if we change the host, then the response is different")

      {
        val dynamicEndpointHostJson = SwaggerDefinitionsJSON.dynamicEndpointHostJson400
        Then("We grant the role to the user1 and update the host")
        Entitlement.entitlement.vend.addEntitlement("", resourceUser1.userId, canUpdateDynamicEndpoint.toString)
        val requestPut = (v4_0_0_Request / "management" / "dynamic-endpoints"/dynamicEndpointId/ "host").PUT<@ (user1)
        val responsePut = makePutRequest(requestPut, write(dynamicEndpointHostJson))

        Then("if we changed the host, the response should be the errors")
        val request = (v4_0_0_Request / "dynamic" / "documents").POST<@ (user1)
        val response = makePostRequest(request, dynamicEndpointSwagger)
        response.code should equal(404)
        response.body.toString contains(EndpointMappingNotFoundByOperationId) should be (true)
      }

    }
  }


}
