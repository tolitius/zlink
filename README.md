
<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# zlink

talking across languages

- [What is it?](#what-is-it)
- [ØMQ based communication](#%C3%B8mq-based-communication)
  - [Start a ØMQ server](#start-a-%C3%B8mq-server)
  - [Start REPL](#start-repl)
  - [Rock & Roll](#rock-&-roll)
  - [Silly Bench](#silly-bench)
    - [Multiple Pipes](#multiple-pipes)
- [gRPC based communication](#grpc-based-communication)
  - [Start a gRPC server](#start-a-grpc-server)
  - [Start REPL](#start-repl-1)
  - [Silly Bench](#silly-bench-1)
- [ZeroMQ & Java Bindings](#zeromq-&-java-bindings)
- [License](#license)

## What is it?

This is an exploratary attempt to have a fast communication between languages. There are many bridges and ways to do that but most of them have a limitation of coupling different langauge environments together, whether it's communcating over C bindings or running one VM inside another.

`zlink` takes a different approach: langauges need to live and run in their own environements while communication should be done over pipes / channels / sockets, etc.

There are more to do:

* good channel/ pipe abstraction
* pluggable tech: i.e. zMQ, gRPC, etc.
* communication topology
* pluggable serialization
* .. and more

## ØMQ based communication

This would run a Python server and have JVM client passing data to it and getting a response back over the ØMQ based pipe.

### Start a ØMQ server

Start a [zmq server](src/zlink/lang/python/server.py) in any language, say Python:

```bash
[zlink]$ python test/python/server.py
```

### Start REPL

In a different terminal navigate to in zlink source root and start a zlink REPL:

```bash
[zlink]$ boot dev
```

### Rock & Roll

```clojure
=> (require '[zlink.core :as z])

;; data is anything that can be converted to bytes, in this case some JSON
=> (def data (.getBytes "[\"answer to the ultimate question of life universe and everything\"]"))

=> (def talk (z/z-pipe "tcp://localhost:5555" z/json-in))
```
```
=> (talk data)
["answer to the ultimate question of life universe and everything" 42]
```

that `42` was added by Python on the [other side](src/zlink/lang/python/server.py#L7).

### Silly Bench

`9,000 chats/s` that's round trips with JSON in and out

```clojure
=> (time (dotimes [_ 9000] (talk data)))
"Elapsed time: 1022.295091 msecs"
```

#### Multiple Pipes

`23,000 chats/s` that's round trips with JSON in and out

```clojure
=> (def talk (z/z-pipes {:host "localhost
                         :start-port 5555
                         :pnum 42
                         :consume z/json-in}))

=> (time (last (mapv #(.get %) (map (fn [_] (talk data)) (range 23000)))))
"Elapsed time: 962.826325 msecs"
["answer to the ultimate question of life universe and everything" 42]
```

_and_ could be improved: serialization, different zmq topology, etc.

## gRPC based communication

### Start a gRPC server

Start a [gRPC server](src/zlink/lang/python/server.py) in any language, say Python:

```bash
[zlink]$ python grpc/python/server.py
```

### Start REPL

In a different terminal navigate to in zlink source root and start a zlink REPL:

```clojure
=> (require '[zlink.grpc.client :as gc])

=> (def piper (gc/piper "localhost" 50051))
#'boot.user/piper
=> piper
#object[io.data.pipe.PiperClient 0x414a1f16 "io.data.pipe.PiperClient@414a1f16"]

=> (def req (gc/request "[\"answer to the ultimate question of life universe and everything\"]"))
#'boot.user/req
=> req
#object[io.data.pipe.DataRequest 0x3cacdc40 "data: \"[\\\"answer to the ultimate question of life universe and everything\\\"]\"\n"]
```
```clojure
boot.user=> (gc/pipe-it piper req)
"[\"answer to the ultimate question of life universe and everything\", 42]"
```

`42` came from the [gRPC python server](grpc/python/server.py#L15)

### Silly Bench

```clojure
boot.user=> (time (dotimes [_ 2700] (gc/pipe-it piper req)))
"Elapsed time: 1004.712208 msecs"
```

## ZeroMQ & Java Bindings

Since "ZeroMQ" is a default queuing mechanism, and zlink is JVM (Clojure), ZeroMQ [libraries](http://www.zeromq.org/intro:get-the-software) and [Java bindings](http://www.zeromq.org/bindings:java) need to be installed, in order for zlink to run.

After ZeroMQ and JZMQ are installed, depending on a version of ZeroMQ (let's say it's `${zeromq.version}`), a zeromq jar can be installed to local maven repo. The `zmq.jar` should live in `/usr/local/share/java/`, after JZMQ is installed. Just copy it somewhere, rename it to have a `${zeromq.version}` (e.g. `zmq-${zeromq.version}.jar`), and:

```bash
mvn install:install-file -Dfile=./zmq-${zeromq.version}.jar -DgroupId=zmq -DartifactId=zmq -Dversion=${zeromq.version} -Dpackaging=jar
```

also export `JAVA_LIBRARY_PATH` to find native libs when running:

```
export JAVA_LIBRARY_PATH=$JAVA_LIBRARY_PATH:/usr/local/lib
```

## License

Copyright © 2019 tolitius

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
