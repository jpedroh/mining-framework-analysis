package com.amazonaws.services.kinesis.multilang;
import static org.mockito.Matchers.any;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.InvalidStateException;
import static org.mockito.Matchers.anyLong;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.KinesisClientLibDependencyException;
import static org.mockito.Matchers.anyString;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ShutdownException;
import static org.mockito.Matchers.argThat;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ThrottlingException;
import static org.mockito.Mockito.never;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import static org.mockito.Mockito.times;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.ShutdownReason;
import static org.mockito.Mockito.verify;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import static org.mockito.Mockito.when;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import java.io.IOException;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import java.io.InputStream;
import com.amazonaws.services.kinesis.model.Record;
import java.io.OutputStream;
import com.amazonaws.services.kinesis.multilang.messages.InitializeMessage;
import java.util.ArrayList;
import com.amazonaws.services.kinesis.multilang.messages.Message;
import java.util.List;
import com.amazonaws.services.kinesis.multilang.messages.ProcessRecordsMessage;
import java.util.concurrent.ExecutionException;
import com.amazonaws.services.kinesis.multilang.messages.ShutdownMessage;
import java.util.concurrent.ExecutorService;
import com.amazonaws.services.kinesis.multilang.messages.StatusMessage;
import java.util.concurrent.Executors;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.Future;
import org.junit.Assert;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IPreparedCheckpointer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import java.util.Optional;

@RunWith(value = MockitoJUnitRunner.class) public class StreamingRecordProcessorTest {
  private static final String shardId = "shard-123";

  private int systemExitCount = 0;

  @Mock private Future<Message> messageFuture;

  private IRecordProcessorCheckpointer unimplementedCheckpointer = new IRecordProcessorCheckpointer() {
    @Override public void checkpoint() throws KinesisClientLibDependencyException, InvalidStateException, ThrottlingException, ShutdownException {
      throw new UnsupportedOperationException();
    }

    @Override public void checkpoint(String sequenceNumber) throws KinesisClientLibDependencyException, InvalidStateException, ThrottlingException, ShutdownException, IllegalArgumentException {
      throw new UnsupportedOperationException();
    }

    @Override public void checkpoint(Record record) throws KinesisClientLibDependencyException, InvalidStateException, ThrottlingException, ShutdownException {
      throw new UnsupportedOperationException();
    }

    @Override public void checkpoint(String sequenceNumber, long subSequenceNumber) throws KinesisClientLibDependencyException, InvalidStateException, ThrottlingException, ShutdownException, IllegalArgumentException {
      throw new UnsupportedOperationException();
    }

    @Override public IPreparedCheckpointer prepareCheckpoint() throws KinesisClientLibDependencyException, InvalidStateException, ThrottlingException, ShutdownException {
      throw new UnsupportedOperationException();
    }

    @Override public IPreparedCheckpointer prepareCheckpoint(Record record) throws KinesisClientLibDependencyException, InvalidStateException, ThrottlingException, ShutdownException {
      throw new UnsupportedOperationException();
    }

    @Override public IPreparedCheckpointer prepareCheckpoint(String sequenceNumber) throws KinesisClientLibDependencyException, InvalidStateException, ThrottlingException, ShutdownException, IllegalArgumentException {
      throw new UnsupportedOperationException();
    }

    @Override public IPreparedCheckpointer prepareCheckpoint(String sequenceNumber, long subSequenceNumber) throws KinesisClientLibDependencyException, InvalidStateException, ThrottlingException, ShutdownException, IllegalArgumentException {
      throw new UnsupportedOperationException();
    }
  };

  private MessageWriter messageWriter;

  private DrainChildSTDERRTask errorReader;

  private MessageReader messageReader;

  private MultiLangRecordProcessor recordProcessor;

  @Mock private KinesisClientLibConfiguration configuration;

  @Before public void prepare() throws IOException, InterruptedException, ExecutionException {
    String command = "derp";
    systemExitCount = 0;
    ExecutorService executor = Executors.newFixedThreadPool(3);
    final Process process = Mockito.mock(Process.class);
    messageWriter = Mockito.mock(MessageWriter.class);
    messageReader = Mockito.mock(MessageReader.class);
    errorReader = Mockito.mock(DrainChildSTDERRTask.class);
    when(configuration.getTimeoutInSeconds()).thenReturn(Optional.empty());
    recordProcessor = new MultiLangRecordProcessor(new ProcessBuilder(), executor, new ObjectMapper(), messageWriter, messageReader, errorReader, configuration) {
      void exit() {
        systemExitCount += 1;
      }

      Process startProcess() {
        return process;
      }
    };
    InputStream inputStream = Mockito.mock(InputStream.class);
    InputStream errorStream = Mockito.mock(InputStream.class);
    OutputStream outputStream = Mockito.mock(OutputStream.class);
    Mockito.doReturn(inputStream).when(process).getInputStream();
    Mockito.doReturn(errorStream).when(process).getErrorStream();
    Mockito.doReturn(outputStream).when(process).getOutputStream();
    Mockito.doReturn(Mockito.mock(Future.class)).when(messageReader).drainSTDOUT();
    Future<Boolean> trueFuture = Mockito.mock(Future.class);
    Mockito.doReturn(true).when(trueFuture).get();
    when(messageWriter.writeInitializeMessage(any(InitializationInput.class))).thenReturn(trueFuture);
    when(messageWriter.writeCheckpointMessageWithError(anyString(), anyLong(), any(Throwable.class))).thenReturn(trueFuture);
    when(messageWriter.writeProcessRecordsMessage(any(ProcessRecordsInput.class))).thenReturn(trueFuture);
    when(messageWriter.writeShutdownMessage(any(ShutdownReason.class))).thenReturn(trueFuture);
  }

  private void phases(Answer<StatusMessage> answer) throws InterruptedException, ExecutionException {
    when(messageFuture.get()).thenAnswer(answer);
    when(messageReader.getNextMessageFromSTDOUT()).thenReturn(messageFuture);
    List<Record> testRecords = new ArrayList<Record>();
    recordProcessor.initialize(new InitializationInput().withShardId(shardId));
    recordProcessor.processRecords(new ProcessRecordsInput().withRecords(testRecords).withCheckpointer(unimplementedCheckpointer));
    recordProcessor.processRecords(new ProcessRecordsInput().withRecords(testRecords).withCheckpointer(unimplementedCheckpointer));
    recordProcessor.shutdown(new ShutdownInput().withCheckpointer(unimplementedCheckpointer).withShutdownReason(ShutdownReason.ZOMBIE));
  }

  @Test public void processorPhasesTest() throws InterruptedException, ExecutionException {
    Answer<StatusMessage> answer = new Answer<StatusMessage>() {
      StatusMessage[] answers = new StatusMessage[] { new StatusMessage(InitializeMessage.ACTION), new StatusMessage(ProcessRecordsMessage.ACTION), new StatusMessage(ProcessRecordsMessage.ACTION), new StatusMessage(ShutdownMessage.ACTION) };

      int callCount = 0;

      @Override public StatusMessage answer(InvocationOnMock invocation) throws Throwable {
        if (callCount < answers.length) {
          return answers[callCount++];
        } else {
          throw new Throwable("Too many calls to getNextStatusMessage");
        }
      }
    };
    phases(answer);
    verify(messageWriter).writeInitializeMessage(argThat(Matchers.withInit(new InitializationInput().withShardId(shardId))));
    verify(messageWriter, times(2)).writeProcessRecordsMessage(any(ProcessRecordsInput.class));
    verify(messageWriter).writeShutdownMessage(ShutdownReason.ZOMBIE);
  }

  @Test public void initFailsTest() throws InterruptedException, ExecutionException {
    Answer<StatusMessage> answer = new Answer<StatusMessage>() {
      StatusMessage[] answers = new StatusMessage[] { new StatusMessage("Bad"), new StatusMessage(ProcessRecordsMessage.ACTION), new StatusMessage(ProcessRecordsMessage.ACTION), new StatusMessage(ShutdownMessage.ACTION) };

      int callCount = 0;

      @Override public StatusMessage answer(InvocationOnMock invocation) throws Throwable {
        if (callCount < answers.length) {
          return answers[callCount++];
        } else {
          throw new Throwable("Too many calls to getNextStatusMessage");
        }
      }
    };
    phases(answer);
    verify(messageWriter).writeInitializeMessage(argThat(Matchers.withInit(new InitializationInput().withShardId(shardId))));
    verify(messageWriter, times(2)).writeProcessRecordsMessage(any(ProcessRecordsInput.class));
    verify(messageWriter, never()).writeShutdownMessage(ShutdownReason.ZOMBIE);
    Assert.assertEquals(1, systemExitCount);
  }
}