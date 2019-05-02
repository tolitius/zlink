from concurrent import futures
import time
import logging
import json

import grpc

import pipe_pb2
import pipe_pb2_grpc

_ONE_DAY_IN_SECONDS = 60 * 60 * 24

def rock_on(event):
    """solves the hardest problem of all"""
    event.append(42)
    return event

class PiperServicer(pipe_pb2_grpc.PiperServicer):

    def Send(self, request, context):
      event = json.loads(request.data)
      return pipe_pb2.DataResponse(data=json.dumps(rock_on(event)))

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=42))
    pipe_pb2_grpc.add_PiperServicer_to_server(
        PiperServicer(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    try:
        while True:
            time.sleep(_ONE_DAY_IN_SECONDS)
    except KeyboardInterrupt:
        server.stop(0)


if __name__ == '__main__':
    logging.basicConfig()
    serve()
