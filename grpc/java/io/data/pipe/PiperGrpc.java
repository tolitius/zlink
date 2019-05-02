package io.data.pipe;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 * <pre>
 * the data pipe service definition
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.8.0)",
    comments = "Source: pipe.proto")
public final class PiperGrpc {

  private PiperGrpc() {}

  public static final String SERVICE_NAME = "pipe.Piper";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSendMethod()} instead. 
  public static final io.grpc.MethodDescriptor<io.data.pipe.DataRequest,
      io.data.pipe.DataResponse> METHOD_SEND = getSendMethod();

  private static volatile io.grpc.MethodDescriptor<io.data.pipe.DataRequest,
      io.data.pipe.DataResponse> getSendMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<io.data.pipe.DataRequest,
      io.data.pipe.DataResponse> getSendMethod() {
    io.grpc.MethodDescriptor<io.data.pipe.DataRequest, io.data.pipe.DataResponse> getSendMethod;
    if ((getSendMethod = PiperGrpc.getSendMethod) == null) {
      synchronized (PiperGrpc.class) {
        if ((getSendMethod = PiperGrpc.getSendMethod) == null) {
          PiperGrpc.getSendMethod = getSendMethod = 
              io.grpc.MethodDescriptor.<io.data.pipe.DataRequest, io.data.pipe.DataResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "pipe.Piper", "Send"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.data.pipe.DataRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.data.pipe.DataResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new PiperMethodDescriptorSupplier("Send"))
                  .build();
          }
        }
     }
     return getSendMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PiperStub newStub(io.grpc.Channel channel) {
    return new PiperStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PiperBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new PiperBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PiperFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new PiperFutureStub(channel);
  }

  /**
   * <pre>
   * the data pipe service definition
   * </pre>
   */
  public static abstract class PiperImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * processes request and sends a response
     * </pre>
     */
    public void send(io.data.pipe.DataRequest request,
        io.grpc.stub.StreamObserver<io.data.pipe.DataResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSendMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSendMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                io.data.pipe.DataRequest,
                io.data.pipe.DataResponse>(
                  this, METHODID_SEND)))
          .build();
    }
  }

  /**
   * <pre>
   * the data pipe service definition
   * </pre>
   */
  public static final class PiperStub extends io.grpc.stub.AbstractStub<PiperStub> {
    private PiperStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PiperStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PiperStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PiperStub(channel, callOptions);
    }

    /**
     * <pre>
     * processes request and sends a response
     * </pre>
     */
    public void send(io.data.pipe.DataRequest request,
        io.grpc.stub.StreamObserver<io.data.pipe.DataResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSendMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * the data pipe service definition
   * </pre>
   */
  public static final class PiperBlockingStub extends io.grpc.stub.AbstractStub<PiperBlockingStub> {
    private PiperBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PiperBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PiperBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PiperBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * processes request and sends a response
     * </pre>
     */
    public io.data.pipe.DataResponse send(io.data.pipe.DataRequest request) {
      return blockingUnaryCall(
          getChannel(), getSendMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * the data pipe service definition
   * </pre>
   */
  public static final class PiperFutureStub extends io.grpc.stub.AbstractStub<PiperFutureStub> {
    private PiperFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PiperFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PiperFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PiperFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * processes request and sends a response
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.data.pipe.DataResponse> send(
        io.data.pipe.DataRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSendMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final PiperImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(PiperImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND:
          serviceImpl.send((io.data.pipe.DataRequest) request,
              (io.grpc.stub.StreamObserver<io.data.pipe.DataResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class PiperBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PiperBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.data.pipe.DataPipeProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Piper");
    }
  }

  private static final class PiperFileDescriptorSupplier
      extends PiperBaseDescriptorSupplier {
    PiperFileDescriptorSupplier() {}
  }

  private static final class PiperMethodDescriptorSupplier
      extends PiperBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    PiperMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (PiperGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PiperFileDescriptorSupplier())
              .addMethod(getSendMethod())
              .build();
        }
      }
    }
    return result;
  }
}
