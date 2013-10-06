package io.netty.protocol.wamp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.protocol.wamp.messages.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SerDesTests {

	@Test
	public void testWelcomeMessageSer() throws Exception {
		String sessionStr = "someSession";
		String serverIdentStr = "someServer";
		WelcomeMessage wmSer = new WelcomeMessage(sessionStr, serverIdentStr);

		final String jsonStr = wmSer.toJson();
		System.out.println(jsonStr);
		final String expectedStr = String.format("[%d,\"%s\",%d,\"%s\"]", wmSer.getMessageCode(), sessionStr, WelcomeMessage.PROTOCOL_VERSION, serverIdentStr);
		Assert.assertEquals(expectedStr, jsonStr);

		WelcomeMessage wmDes = WelcomeMessage.fromJson(expectedStr);
		Assert.assertNotNull(wmDes);
		Assert.assertTrue(wmDes.equals(wmSer));
	}

	@Test
	public void testPrefixMessage() throws Exception {
		String prefixStr = "somePrefix";
		String uri = "http://api.dotts.net/games";
		PrefixMessage pmSer = new PrefixMessage(prefixStr, uri);

		final String jsonStr = pmSer.toJson();
		System.out.println(jsonStr);
		final String expectedStr = String.format("[%d,\"%s\",\"%s\"]", pmSer.getMessageCode(), prefixStr, uri);
		Assert.assertEquals(expectedStr, jsonStr);

		PrefixMessage pmDes = PrefixMessage.fromJson(expectedStr);
		Assert.assertNotNull(pmDes);
		Assert.assertTrue(pmDes.equals(pmSer));
	}

	@Test
	public void testCallMessage() throws Exception {
		String callIdStr = "someCallId";
		String procURI = "http://api.dotts.net/games/start";
		CallMessage cmSer = new CallMessage(callIdStr, procURI);

		// Test1
		final String jsonStr1 = cmSer.toJson();
		System.out.println(jsonStr1);
		final String expectedStr1 = String.format("[%d,\"%s\",\"%s\"]", cmSer.getMessageCode(), callIdStr, procURI);
		Assert.assertEquals(expectedStr1, jsonStr1);

		// Test2
		cmSer.args = new ArrayList<>(1);
		cmSer.args.add(null);
		final String jsonStr2 = cmSer.toJson();
		System.out.println(jsonStr2);
		final String expectedStr2 = String.format("[%d,\"%s\",\"%s\",%s]", cmSer.getMessageCode(), callIdStr, procURI, "null");
		Assert.assertEquals(expectedStr2, jsonStr2);

		// Test3
		ObjectMapper mapper = MessageMapper.objectMapper;

		cmSer.args = new ArrayList<>(2);
		cmSer.args.add(mapper.valueToTree(getExampleMap()));
		cmSer.args.add(mapper.valueToTree(new int[] {1,2}));

		final String jsonStr3 = cmSer.toJson();
		System.out.println(jsonStr3);
		final String expectedStr3 = String.format("[%d,\"%s\",\"%s\",%s]", cmSer.getMessageCode(), callIdStr, procURI, exampleStr + ",[1,2]");
		Assert.assertEquals(expectedStr3, jsonStr3);

		CallMessage cmDes = CallMessage.fromJson(expectedStr3);
		Assert.assertNotNull(cmDes);
		Assert.assertTrue(cmDes.toJson().equals(jsonStr3));
	}

	@Test
	public void testCallResultMessage() throws Exception {
		String callIdStr = "someCallId";
		CallResultMessage crmSer = new CallResultMessage(callIdStr, null);

		// Test1
		final String jsonStr1 = crmSer.toJson();
		System.out.println(jsonStr1);
		final String expectedStr1 = String.format("[%d,\"%s\",%s]", crmSer.getMessageCode(), callIdStr, "null");
		Assert.assertEquals(expectedStr1, jsonStr1);

		// Test2
		crmSer.result = getExampleMap();
		final String jsonStr2 = crmSer.toJson();
		System.out.println(jsonStr2);
		final String expectedStr2 = String.format("[%d,\"%s\",%s]", crmSer.getMessageCode(), callIdStr, exampleStr);
		Assert.assertEquals(expectedStr2, jsonStr2);

		CallResultMessage crmDes = CallResultMessage.fromJson(expectedStr2);
		Assert.assertNotNull(crmDes);
		Assert.assertTrue(crmDes.toJson().equals(jsonStr2));
	}

	@Test
	public void testCallErrorMessage() throws Exception {
		String callIdStr = "someCallId";
		String errorURI = "http://api.dotts.net/games/start/fail";
		String errorDesc = "No such users";
		CallErrorMessage cemSer = new CallErrorMessage(callIdStr, errorURI, errorDesc);

		// Test1
		final String jsonStr1 = cemSer.toJson();
		System.out.println(jsonStr1);
		final String expectedStr1 = String.format("[%d,\"%s\",\"%s\",\"%s\"]", cemSer.getMessageCode(), callIdStr, errorURI, errorDesc);
		Assert.assertEquals(expectedStr1, jsonStr1);

		// Test2
		cemSer.errorDetails = new int[] {123,45};
		final String jsonStr2 = cemSer.toJson();
		System.out.println(jsonStr2);
		final String expectedStr2 = String.format("[%d,\"%s\",\"%s\",\"%s\",%s]", cemSer.getMessageCode(), callIdStr, errorURI, errorDesc, "[123,45]");
		Assert.assertEquals(expectedStr2, jsonStr2);

		CallErrorMessage cemDes = CallErrorMessage.fromJson(expectedStr2);
		Assert.assertNotNull(cemDes);
		Assert.assertTrue(cemDes.toJson().equals(jsonStr2));
	}

	@Test
	public void testSubscribeMessages() throws Exception {
		String topicUri = "http://api.dotts.net/games;id=12345";

		SubscribeMessage smSer = new SubscribeMessage(topicUri);
		final String jsonStr1 = smSer.toJson();
		System.out.println(jsonStr1);
		final String expectedStr1 = String.format("[%d,\"%s\"]", smSer.getMessageCode(), topicUri);
		Assert.assertEquals(expectedStr1, jsonStr1);

		UnsubscribeMessage usmSer = new UnsubscribeMessage(topicUri);
		final String jsonStr2 = usmSer.toJson();
		System.out.println(jsonStr2);
		final String expectedStr2 = String.format("[%d,\"%s\"]", usmSer.getMessageCode(), topicUri);
		Assert.assertEquals(expectedStr2, jsonStr2);

		SubscribeMessage smDes = SubscribeMessage.fromJson(expectedStr1);
		Assert.assertNotNull(smDes);
		Assert.assertTrue(smDes.toJson().equals(jsonStr1));

		UnsubscribeMessage usmDes = UnsubscribeMessage.fromJson(expectedStr2);
		Assert.assertNotNull(usmDes);
		Assert.assertTrue(usmDes.toJson().equals(jsonStr2));
	}

	@Test
	public void testPublishMessage() throws Exception {
		String topicUri = "http://api.dotts.net/games;id=12345";
		PublishMessage pmSer = new PublishMessage(topicUri, new int[] {23,45});

		// Test1
		final String jsonStr1 = pmSer.toJson();
		System.out.println(jsonStr1);
		final String expectedStr1 = String.format("[%d,\"%s\",%s]", pmSer.getMessageCode(), topicUri, "[23,45]");
		Assert.assertEquals(expectedStr1, jsonStr1);

		pmSer.excludeMe = false;
		Assert.assertEquals(expectedStr1, pmSer.toJson());

		// Test2
		pmSer.excludeMe = true;
		final String jsonStr2 = pmSer.toJson();
		System.out.println(jsonStr2);
		final String expectedStr2 = String.format("[%d,\"%s\",%s,%s]", pmSer.getMessageCode(), topicUri, "[23,45]", "true");
		Assert.assertEquals(expectedStr2, jsonStr2);

		PublishMessage pmDes = PublishMessage.fromJson(expectedStr2);
		Assert.assertNotNull(pmDes);
		Assert.assertTrue(pmDes.toJson().equals(jsonStr2));

		// Test3
		pmSer.excludeMe = false;
		pmSer.exclude = new ArrayList<>(2);
		pmSer.exclude.add("qwe");
		pmSer.exclude.add("asd");
		pmSer.eligible = new ArrayList<>(2);
		pmSer.eligible.add("ewq");
		pmSer.eligible.add("dsa");
		final String jsonStr3 = pmSer.toJson();
		System.out.println(jsonStr3);
		final String expectedStr3 = String.format("[%d,\"%s\",%s,%s,%s]", pmSer.getMessageCode(), topicUri, "[23,45]", "[\"qwe\",\"asd\"]", "[\"ewq\",\"dsa\"]");
		Assert.assertEquals(expectedStr3, jsonStr3);

		// Test 4
		pmSer.exclude = null;
		final String jsonStr4 = pmSer.toJson();
		System.out.println(jsonStr4);
		final String expectedStr4 = String.format("[%d,\"%s\",%s,%s,%s]", pmSer.getMessageCode(), topicUri, "[23,45]", "[]", "[\"ewq\",\"dsa\"]");
		Assert.assertEquals(expectedStr4, jsonStr4);

		pmSer.exclude = new ArrayList<>(1);
		Assert.assertEquals(expectedStr4, pmSer.toJson());

		pmDes = PublishMessage.fromJson(expectedStr4);
		Assert.assertNotNull(pmDes);
		Assert.assertTrue(pmDes.toJson().equals(jsonStr4));
	}

	@Test
	public void testEventMessage() throws Exception {
		String topicUri = "http://api.dotts.net/games;id=12345";
		EventMessage emSer = new EventMessage(topicUri, getExampleMap());

		final String jsonStr1 = emSer.toJson();
		System.out.println(jsonStr1);
		final String expectedStr1 = String.format("[%d,\"%s\",%s]", emSer.getMessageCode(), topicUri, exampleStr);
		Assert.assertEquals(expectedStr1, jsonStr1);

		EventMessage emDes = EventMessage.fromJson(expectedStr1);
		Assert.assertNotNull(emDes);
		Assert.assertTrue(emDes.toJson().equals(jsonStr1));
	}

	final static String exampleStr = "{\"verified\":false,\"name\":{\"last\":\"Sixpack\",\"first\":\"Joe\"},\"userImage\":\"Rm9vYmFyIQ==\",\"gender\":\"MALE\"}";

	private static Map<String, Object> getExampleMap() {
		Map<String,Object> userData = new HashMap<>();
		Map<String,String> nameStruct = new HashMap<>();
		nameStruct.put("first", "Joe");
		nameStruct.put("last", "Sixpack");
		userData.put("name", nameStruct);
		userData.put("gender", "MALE");
		userData.put("verified", Boolean.FALSE);
		userData.put("userImage", "Rm9vYmFyIQ==");
		return userData;
	}
}
