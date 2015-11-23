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
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

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
        if (System.getProperty("os.name").startsWith("Windows")) {
            EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new RemoteDebuggerHandler(taskExecutor, template));
            b.bind(PORT).sync().channel().closeFuture();
            return b;
        } else {
            EventLoopGroup group = new EpollEventLoopGroup(); // Windows 기반에서는 Epoll 동작 안함.
            Bootstrap b = new Bootstrap();
            b.group(group).channel(EpollDatagramChannel.class).option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).handler(
                    new RemoteDebuggerHandler(taskExecutor, template));
            b.bind(PORT).sync().channel().closeFuture();
            return b;
        }
    }
}
