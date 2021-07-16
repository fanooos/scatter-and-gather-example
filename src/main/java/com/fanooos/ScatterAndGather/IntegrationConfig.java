package com.fanooos.ScatterAndGather;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.aggregator.AggregatingMessageHandler;
import org.springframework.integration.aggregator.DefaultAggregatingMessageGroupProcessor;
import org.springframework.integration.aggregator.HeaderAttributeCorrelationStrategy;
import org.springframework.integration.aggregator.MessageCountReleaseStrategy;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.expression.ValueExpression;
import org.springframework.integration.router.HeaderValueRouter;
import org.springframework.messaging.MessageHandler;

import com.fanooos.ScatterAndGather.service.JsonPlaceHolderRequestor;
import com.fanooos.ScatterAndGather.service.ReqResRequestor;

@Configuration
@EnableIntegration
public class IntegrationConfig {

	private final String headerName = "RESOURCE_DOMAIN";
	private final String correlationHeader = "USER_ID";

	@ServiceActivator(inputChannel = "aggregatorInputChannel")
	@Bean
	public AggregatingMessageHandler aggregator() {
		AggregatingMessageHandler aggregator = new AggregatingMessageHandler(new DefaultAggregatingMessageGroupProcessor());
		aggregator.setGroupTimeoutExpression(new ValueExpression<>(500L));
		aggregator.setReleaseStrategy(new MessageCountReleaseStrategy(4));
		aggregator.setExpireGroupsUponCompletion(true);
		aggregator.setCorrelationStrategy(new HeaderAttributeCorrelationStrategy(correlationHeader));
		aggregator.setOutputChannel(resultsChannel());
		return aggregator;
	}
	
	
	@Bean
	public DirectChannel aggregatorInputChannel() {
		return new DirectChannel();
	}
	
	@Bean
	public QueueChannel resultsChannel() {
		return new QueueChannel();
	}

	@Bean
	public HeaderValueRouter headerValueRouter() {
		HeaderValueRouter router = new HeaderValueRouter(headerName);
		router.setChannelMapping("reqres", "reqresInputChannel");
		router.setChannelMapping("jsonplaceholder", "jsonplaceholderInputChannel");
		return router;
	}

	@Bean(name = "reqresInputChannel")
	public DirectChannel reqresInputChannel() {
		return new DirectChannel();
	}

	@Bean(name = "jsonplaceholderInputChannel")
	public DirectChannel jsonplaceholderInputChannel() {
		return new DirectChannel();
	}

	@Bean
	public MessageHandler reqResHandler() {
		ReqResRequestor reqResMessageHandler = new ReqResRequestor();
		reqresInputChannel().subscribe(reqResMessageHandler);
		return reqResMessageHandler;
	}

	@Bean
	public MessageHandler jsonPlaceHolderHandler() {
		JsonPlaceHolderRequestor jsonPlaceHolderMessageHandler = new JsonPlaceHolderRequestor();
		jsonplaceholderInputChannel().subscribe(jsonPlaceHolderMessageHandler);
		return jsonPlaceHolderMessageHandler;
	}
	
}
