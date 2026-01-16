package fr.insee.pearljam.api.controller;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomStompSessionHandler extends StompSessionHandlerAdapter {

	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		log.info("New session established : {}", session.getSessionId());
	}

	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
			Throwable exception) {
		log.error("Got an exception", exception);
	}
}