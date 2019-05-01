import json
import zmq
import getopt
import multiprocessing as mp

def rock_on(event):
    event.append(42)
    return event

def rep_server(process, host, port):

    context = zmq.Context()
    socket = context.socket(zmq.REP)
    socket.bind("tcp://" + host + ":" + str(port))

    print("listening on: %s:%d" % (host, port))

    while True:
	message = socket.recv()
	# print("received [%d]: %s" % (port, message))
	event = json.loads(message)
	socket.send(json.dumps(process(event)))

def start_rep(start_from_port, server_number, fn):

    for port in range(start_from_port, server_number + start_from_port):
	mp.Process(target=rep_server, args=(fn, "*", port)).start()

if __name__ == '__main__':
    start_rep(start_from_port=5555, server_number=1, fn=rock_on)

