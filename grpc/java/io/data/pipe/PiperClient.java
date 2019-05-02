package io.data.pipe;

import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Message;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.data.pipe.PiperGrpc.PiperBlockingStub;
import io.data.pipe.PiperGrpc.PiperStub;
import io.data.pipe.DataRequest;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sample client code that makes gRPC calls to the server.
 */
public class PiperClient {
  private static final Logger logger = Logger.getLogger(PiperClient.class.getName());

  private final ManagedChannel channel;
  public final PiperBlockingStub blockingStub;
  public final PiperStub asyncStub;

  /** Construct client for accessing Piper server at {@code host:port}. */
  public PiperClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
  }

  /** Construct client for accessing Piper server using the existing channel. */
  public PiperClient(ManagedChannelBuilder<?> channelBuilder) {
    channel = channelBuilder.build();
    blockingStub = PiperGrpc.newBlockingStub(channel);
    asyncStub = PiperGrpc.newStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  /** Issues several different requests and then exits. */
  public static void main(String[] args) throws InterruptedException {

    PiperClient piper = new PiperClient("localhost", 50051);

    try {
      DataRequest req = DataRequest.newBuilder().setData("[]").build();
      System.out.println( "sent and got:" + piper.blockingStub.send(req));
    } finally {
      piper.shutdown();
    }
  }
}
