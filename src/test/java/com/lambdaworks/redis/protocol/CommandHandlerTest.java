package com.lambdaworks.redis.protocol;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;

import java.util.ArrayDeque;

import java.util.Queue;

import java.util.concurrent.Future;

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/protocol/CommandHandlerTest.java/left.java
import org.junit.Before;
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/protocol/CommandHandlerTest.java/base.java
=======
import org.junit.Before;

import org.junit.Before;
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/protocol/CommandHandlerTest.java/right.java

import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.invocation.InvocationOnMock;

import org.mockito.runners.MockitoJUnitRunner;

import org.mockito.stubbing.Answer;

import com.lambdaworks.redis.ClientOptions;

import com.lambdaworks.redis.ConnectionEvents;

import com.lambdaworks.redis.RedisException;

import com.lambdaworks.redis.codec.Utf8StringCodec;

import com.lambdaworks.redis.output.StatusOutput;

import com.lambdaworks.redis.resource.ClientResources;

import io.netty.buffer.ByteBufAllocator;

import io.netty.channel.*;

import static org.mockito.Mockito.verifyZeroInteractions;

import static org.mockito.Mockito.when;

import io.netty.channel.ChannelPromise;

import io.netty.channel.DefaultChannelPromise;

import io.netty.channel.ChannelPipeline;

import io.netty.channel.EventLoop;

import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class CommandHandlerTest {

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/protocol/CommandHandlerTest.java/left.java
    private Queue<RedisCommand<String, String, ?>> q = new ArrayDeque<RedisCommand<String, String, ?>>(10);
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/protocol/CommandHandlerTest.java/base.java
=======
    private Queue<RedisCommand<String, String, ?>> q = new ArrayDeque<>(10);
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/protocol/CommandHandlerTest.java/right.java

    private CommandHandler<String, String> sut;

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/protocol/CommandHandlerTest.java/left.java
    private Command<String, String, String> command = new Command<String, String, String>(CommandType.APPEND,
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/protocol/CommandHandlerTest.java/base.java
    private Command<String, String, String> command = new (CommandType.APPEND,
=======
    private Command<String, String, String> command = new Command<>(CommandType.APPEND,
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/protocol/CommandHandlerTest.java/right.java
            new StatusOutput<String, String>(new Utf8StringCodec()), null);

    @Mock
    private ChannelHandlerContext context;

    @Mock
    private Channel channel;

    @Mock
    private ByteBufAllocator byteBufAllocator;

    @Mock
    private ChannelPipeline pipeline;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ClientResources clientResources;

    @Before
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/protocol/CommandHandlerTest.java/left.java
    public void before() throws Exception {
        when(context.channel()).thenReturn(channel);
        when(context.alloc()).thenReturn(byteBufAllocator);
        when(channel.pipeline()).thenReturn(pipeline);
        when(channel.eventLoop()).thenReturn(eventLoop);
        when(eventLoop.submit(any(Runnable.class))).thenAnswer(new Answer<Future<?>>() {
            @Override
            public Future<?> answer(InvocationOnMock invocation) throws Throwable {
                Runnable r = (Runnable) invocation.getArguments()[0];
                r.run();
                return null;
            }
        });

        when(channel.write(any())).thenAnswer(new Answer<ChannelPromise>() {
            @Override
            public ChannelPromise answer(InvocationOnMock invocation) throws Throwable {
                return new DefaultChannelPromise(channel);
            }
        });

        when(channel.writeAndFlush(any())).thenAnswer(new Answer<ChannelPromise>() {
            @Override
            public ChannelPromise answer(InvocationOnMock invocation) throws Throwable {
                return new DefaultChannelPromise(channel);
            }
        });

        sut = new CommandHandler<String, String>(ClientOptions.create(), clientResources, q);
    }
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/protocol/CommandHandlerTest.java/base.java
    public void before() throws Exception 
=======
    public void before() throws Exception {
        when(context.channel()).thenReturn(channel);
        when(context.alloc()).thenReturn(byteBufAllocator);
        when(channel.pipeline()).thenReturn(pipeline);
        when(channel.eventLoop()).thenReturn(eventLoop);
        when(eventLoop.submit(any(Runnable.class))).thenAnswer(invocation -> {
            Runnable r = (Runnable) invocation.getArguments()[0];
            r.run();
            return null;
        });

        when(channel.write(any())).thenAnswer(invocation -> new DefaultChannelPromise(channel));

        when(channel.writeAndFlush(any())).thenAnswer(invocation -> new DefaultChannelPromise(channel));

        sut = new CommandHandler<String, String>(ClientOptions.create(), clientResources, q);
    }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/protocol/CommandHandlerTest.java/right.java

    @Test
    public void testChannelActive() throws Exception {
        sut.channelRegistered(context);

        sut.channelActive(context);

        verify(pipeline).fireUserEventTriggered(any(ConnectionEvents.Activated.class));

    }

    @Test
    public void testExceptionChannelActive() throws Exception {
        sut.setState(CommandHandler.LifecycleState.ACTIVE);

        when(channel.isActive()).thenReturn(true);

        sut.channelActive(context);
        sut.exceptionCaught(context, new Exception());
    }

    @Test
    public void testIOExceptionChannelActive() throws Exception {
        sut.setState(CommandHandler.LifecycleState.ACTIVE);

        when(channel.isActive()).thenReturn(true);

        sut.channelActive(context);
        sut.exceptionCaught(context, new IOException("Connection timed out"));
    }

    @Test
    public void testExceptionChannelInactive() throws Exception {
        sut.setState(CommandHandler.LifecycleState.DISCONNECTED);
        sut.exceptionCaught(context, new Exception());
        verify(context, never()).fireExceptionCaught(any(Exception.class));
    }

    @Test
    public void testExceptionWithQueue() throws Exception {
        sut.setState(CommandHandler.LifecycleState.ACTIVE);
        q.clear();

        sut.channelActive(context);
        when(channel.isActive()).thenReturn(true);

        q.add(command);
        sut.exceptionCaught(context, new Exception());

        assertThat(q).isEmpty();
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/protocol/CommandHandlerTest.java/left.java
        assertThat(command.getException()).isNotNull();
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/protocol/CommandHandlerTest.java/base.java
        assertThat(command.getException()).isNotNull();

        verify(context).fireExceptionCaught(any(Exception.class));
=======
        command.get();

        assertThat(ReflectionTestUtils.getField(command, "exception")).isNotNull();
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/protocol/CommandHandlerTest.java/right.java
    }

    @Test(expected = RedisException.class)
    public void testWriteWhenClosed() throws Exception {

        sut.setState(CommandHandler.LifecycleState.CLOSED);

        sut.write(command);
    }

    @Test
    public void testExceptionWhenClosed() throws Exception {

        sut.setState(CommandHandler.LifecycleState.CLOSED);

        sut.exceptionCaught(context, new Exception());
        verifyZeroInteractions(context);
    }

}
