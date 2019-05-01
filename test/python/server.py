import cProfile
import time
import json
import zmq

def rock_on(event):
    event.append(42)
    return event

if __name__ == '__main__':

	context = zmq.Context()
	socket = context.socket(zmq.REP)
	socket.bind("tcp://*:5555")

	while True:
	    message = socket.recv()
	    # print("received: %s" % message)

	    event = json.loads(message)
	    socket.send(json.dumps(rock_on(event)))
