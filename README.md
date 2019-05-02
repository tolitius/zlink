# zlink

talking across languages

## Data Talks

start a [zmq server](test/python/server.py) in any language, say Python:

```bash
[zlink]$ python test/python/server.py
```

in a different terminal start zlink REPL:

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

that `42` was added by Python on the [other side](test/python/server.py#L7).

## Silly Bench

`9,000 chats/s` that's round trips with JSON in and out

```clojure
=> (time (dotimes [_ 9000] (talk data)))
"Elapsed time: 1022.295091 msecs"
```

### multiple pipes

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
