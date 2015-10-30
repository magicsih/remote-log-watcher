package app.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import app.handler.RemoteDebuggerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;

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
        EventLoopGroup group = new EpollEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(EpollDatagramChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(EpollChannelOption.SO_REUSEPORT, true)
                .handler(new RemoteDebuggerHandler(taskExecutor, template));
        b.bind(PORT).sync().channel().closeFuture();
        return b;
    }
}
