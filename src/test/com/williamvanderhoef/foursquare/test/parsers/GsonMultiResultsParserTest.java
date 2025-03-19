package com.williamvanderhoef.foursquare.test.parsers;

import java.lang.reflect.Type;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.reflect.TypeToken;
import com.williamvanderhoef.foursquare.BaseTest;
import com.williamvanderhoef.foursquare.adapters.DefinedType;
import com.williamvanderhoef.foursquare.model.subtypes.Results;
import com.williamvanderhoef.foursquare.parsers.GsonResultsParser;
import com.williamvanderhoef.foursquare.responses.GetResponseBase;
import com.williamvanderhoef.foursquare.responses.Responses;
import com.williamvanderhoef.foursquare.responses.ResultsTypes;
import com.williamvanderhoef.foursquare.responses.UserResponse;
import com.williamvanderhoef.foursquare.responses.UserBadgesResponse;

/**
 * 
 * This test requires GSON
 *
 */
public class GsonMultiResultsParserTest extends BaseTest<Responses<Results<UserResponse>, Results<UserBadgesResponse>>>{

	
	
	public static class CustomMultiResponse extends Responses<Results<UserResponse>, Results<UsersBadgesResponse>> implements DefinedType, GetResponseBase{

		@Override
		public Type defineType() {
<<<<<<< /usr/src/app/output/thepoofy/foursquare_v2_java_adapter/dacecfc813308901bc17bdf1bc849c3ca820eb01/src/test/com/williamvanderhoef/foursquare/test/parsers/GsonMultiResultsParserTest.java/left.java
			return this.getClass();
||||||| /usr/src/app/output/thepoofy/foursquare_v2_java_adapter/dacecfc813308901bc17bdf1bc849c3ca820eb01/src/test/com/williamvanderhoef/foursquare/test/parsers/GsonMultiResultsParserTest.java/base.java
			return new TypeToken<Results<Responses<Results<UserResponse>, Results<UsersBadgesResponse>>>>() {}.getType();
=======
			return new TypeToken<Results<Responses<ResultsTypes.UserResults, ResultsTypes.UsersBadgesResults>>>() {}.getType();
>>>>>>> /usr/src/app/output/thepoofy/foursquare_v2_java_adapter/dacecfc813308901bc17bdf1bc849c3ca820eb01/src/test/com/williamvanderhoef/foursquare/test/parsers/GsonMultiResultsParserTest.java/right.java
		}
	}
	
	public static DefinedType getTypeDefinition()
	{
		//Results<Responses<Results<UserResponse>, Results<UsersBadgesResponse>>> results = new Results<Responses<Results<UserResponse>, Results<UsersBadgesResponse>>>(){};
<<<<<<< /usr/src/app/output/thepoofy/foursquare_v2_java_adapter/dacecfc813308901bc17bdf1bc849c3ca820eb01/src/test/com/williamvanderhoef/foursquare/test/parsers/GsonMultiResultsParserTest.java/left.java
		CustomMultiResponse results = new CustomMultiResponse();
||||||| /usr/src/app/output/thepoofy/foursquare_v2_java_adapter/dacecfc813308901bc17bdf1bc849c3ca820eb01/src/test/com/williamvanderhoef/foursquare/test/parsers/GsonMultiResultsParserTest.java/base.java
		Results<Responses<Results<UserResponse>, Results<UsersBadgesResponse>>> results = new Results<Responses<Results<UserResponse>, Results<UsersBadgesResponse>>>(){};
=======
		Results<Responses<ResultsTypes.UserResults, ResultsTypes.UsersBadgesResults>> results  = new Results<Responses<ResultsTypes.UserResults, ResultsTypes.UsersBadgesResults>>();
>>>>>>> /usr/src/app/output/thepoofy/foursquare_v2_java_adapter/dacecfc813308901bc17bdf1bc849c3ca820eb01/src/test/com/williamvanderhoef/foursquare/test/parsers/GsonMultiResultsParserTest.java/right.java
	// 	Results<Responses<Results<UserResponse>, Results<UsersBadgesResponse>>> results = new Results<Responses<Results<UserResponse>, Results<UsersBadgesResponse>>>(){};
		
		
		return results;
	}
	
	@Override
	public String getFileName()
	{
		return "src/test/v2.multi.20110904.json";
	}
	
	@Test
	public void endpointTest()
	{
		DefinedType endpoint = getTypeDefinition();
		
		Type endpointType = endpoint.defineType();
		
		
		Type tokenizedType = new TypeToken<Results<Responses<Results<UserResponse>, Results<UserBadgesResponse>>>>() {}.getType();

		Assert.assertEquals(tokenizedType, endpointType);
	}
	
	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		
		GsonResultsParser<Responses<Results<UserResponse>, Results<UserBadgesResponse>>> gLoader = new GsonResultsParser<Responses<Results<UserResponse>, Results<UserBadgesResponse>>>(getTypeDefinition());
		
		Results<Responses<Results<UserResponse>, Results<UserBadgesResponse>>> results = gLoader.parse(this.getFileContents());
		
		this.setResults(results);
	}
	
	@Test
	public void testUser()
	{
		Results<UserResponse> user = this.getResults().getResponse().getResult1();
		Results<UsersBadgesResponse> badges = this.getResults().getResponse().getResult2();
		
		Assert.assertNotNull(user);
		Assert.assertNotNull(badges);
	}
	
	@Test
	public void TestBadges()
	{
		
	}
}
