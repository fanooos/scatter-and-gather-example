package com.fanooos.ScatterAndGather.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.web.client.RestTemplate;

import com.fanooos.ScatterAndGather.model.ReqResUser;
import com.fanooos.ScatterAndGather.model.User;

public class ReqResRequestor implements MessageHandler {

	private final String correlationHeader = "USER_ID";
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private DirectChannel aggregatorInputChannel;
	
	@Override
	public void handleMessage(Message<?> message) throws MessagingException {
		System.out.println("ReqResRequestor received a message");
		try {
			ReqResUser apiUser = restTemplate.getForObject(
					message.getPayload().toString(), ReqResUser.class);
			User user = new User();
			user.setEmail(apiUser.getData().getEmail());
			user.setId(apiUser.getData().getId());
			user.setName(apiUser.getData().getFirstName() + " " + apiUser.getData().getLastName());
			aggregatorInputChannel.send(MessageBuilder.withPayload(user).setHeader(correlationHeader, "user_1").build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
