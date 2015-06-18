package com.netmarble.unity.debugger.app.configuration;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.netmarble.unity.debugger.app.handler.RemoteDebuggerHandler;

@Configuration
public class UdpConfig {

	private static final int PORT = Integer.parseInt(System.getProperty("port", "7686"));

	@Autowired
	@Qualifier("taskExecutor")
	private TaskExecutor taskExecutor;

	@Autowired
	private SimpMessagingTemplate template;

	@Bean
	public Bootstrap bootstrap() throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true).handler(new RemoteDebuggerHandler(taskExecutor, template));
		b.bind(PORT).sync().channel().closeFuture();
		return b;
	}
}
