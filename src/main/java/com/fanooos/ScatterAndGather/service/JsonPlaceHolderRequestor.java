package com.fanooos.ScatterAndGather.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.web.client.RestTemplate;

import com.fanooos.ScatterAndGather.model.User;

public class JsonPlaceHolderRequestor implements MessageHandler {

	private final String correlationHeader = "USER_ID";
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private DirectChannel aggregatorInputChannel;
	
	@Override
	public void handleMessage(Message<?> message) throws MessagingException {
		try {
			User user = restTemplate.getForObject(message.getPayload().toString(), User.class);
			aggregatorInputChannel.send(MessageBuilder.withPayload(user).setHeader(correlationHeader, "user_1").build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
