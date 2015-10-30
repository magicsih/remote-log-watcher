package app.handler;

import org.apache.log4j.Logger;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import app.task.DatagramPacketHandlingTask;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class RemoteDebuggerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    final static Logger LOGGER = Logger.getLogger(RemoteDebuggerHandler.class);

    private TaskExecutor taskExecutor;

    private SimpMessagingTemplate template;

    public RemoteDebuggerHandler(TaskExecutor taskExecutor, SimpMessagingTemplate template) {
        super();
        this.taskExecutor = taskExecutor;
        this.template = template;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        taskExecutor.execute(new DatagramPacketHandlingTask(template, packet));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}
