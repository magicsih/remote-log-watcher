package com.netmarble.unity.debugger.app.task;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.netmarble.unity.debugger.app.model.Log;

public class LogPropagationTask implements Runnable {

	private final Log log;
	private final SimpMessagingTemplate template;
	private final String topic;

	public LogPropagationTask(String topic, Log log, SimpMessagingTemplate template) {
		super();
		this.topic = topic;
		this.log = log;
		this.template = template;
	}

	@Override
	public void run() {
		template.convertAndSend("/topic/" + topic, log);
	}
}
