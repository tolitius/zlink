import zmq
import json
import threading
import time
from random import randint, random

## adopted sample from: http://zguide.zeromq.org/py:asyncsrv

def rock_on(event):
    event.append(42)
    return event

class ServerTask(threading.Thread):
    """ServerTask"""
    def __init__(self, host, port, n_workers, worker_fn):
        threading.Thread.__init__ (self)
	self.host, self.port, self.n_workers, self.worker_fn = host, port, n_workers, worker_fn

    def run(self):
        context = zmq.Context()
        frontend = context.socket(zmq.ROUTER)
        frontend.bind('tcp://' + self.host + ':' + str(self.port))

        backend = context.socket(zmq.DEALER)
        backend.bind('inproc://backend')

        workers = []
        for i in range(self.n_workers):
            worker = ServerWorker(context, self.worker_fn)
	    worker.daemon = True
            worker.start()
            workers.append(worker)

        zmq.proxy(frontend, backend)

        frontend.close()
        backend.close()
        context.term()

class ServerWorker(threading.Thread):
    """ServerWorker"""
    def __init__(self, context, fn):
        threading.Thread.__init__ (self)
        self.context = context
	self.fn = fn

    def run(self):
        worker = self.context.socket(zmq.DEALER)
        worker.connect('inproc://backend')
        print('worker started')
        while True:
            ident, msg = worker.recv_multipart()
            # print('worker received %s from %s' % (msg, ident))
	    event = json.loads(msg)
	    worker.send_multipart([ident, json.dumps(self.fn(event))])
            # print('worker sent %s back' % json.dumps(self.fn(event)))

        worker.close()

def main():
    """main function"""
    server = ServerTask(host="*", port=5556, n_workers=42, worker_fn=rock_on)
    server.daemon = True
    server.start()
    server.join()

if __name__ == "__main__":
    main()
