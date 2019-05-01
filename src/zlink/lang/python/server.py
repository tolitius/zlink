import time
import json
import zmq
import multiprocessing as mp

def rock_on(event):
    event.append(42)
    return event

def listen(process, host, port):

    context = zmq.Context()
    socket = context.socket(zmq.REP)
    socket.bind("tcp://" + host + ":" + str(port))

    print("listening on: %s:%d" % (host, port))

    while True:
	message = socket.recv()
	# print("received [%d]: %s" % (port, message))
	event = json.loads(message)
	socket.send(json.dumps(process(event)))


if __name__ == '__main__':
    start_from_port = 5555
    # server_number = 42
    server_number = 1

    for port in range(start_from_port, server_number + start_from_port):
	mp.Process(target=listen, args=(rock_on, "*", port)).start()
