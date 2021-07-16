package com.fanooos.ScatterAndGather.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.router.HeaderValueRouter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import com.fanooos.ScatterAndGather.model.User;

@Service
public class UserService {

	@Value("${app.external.resources.urls}")
	private List<String> urls;

	private final String headerName = "RESOURCE_DOMAIN";
	
	
	@Autowired
	private HeaderValueRouter router;

	@Autowired
	private QueueChannel resultsChannel;

	public List<User> getUsers() {
		this.broadcast();
		List<User> users = new ArrayList<>();
		Message<?> message = resultsChannel.receive();
		if (Objects.nonNull(message)) {
			users = (List<User>) message.getPayload();
		}
		return users;
	}

	private void broadcast() {
		System.out.println("Before Broadcasting");
		urls.stream().forEach(url -> {
			String domain = url.split("\\|")[0];
			String resourceurl = url.split("\\|")[1].substring(0);
			Message<String> message = MessageBuilder.withPayload(resourceurl).setHeader(headerName, domain).build();
			router.handleMessage(message);
		});
		System.out.println("After Broadcasting");
	}

}
