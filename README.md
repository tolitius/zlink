# zlink

talking across languages

## Data Talks

start a zmq server in any language, say Python:

```bash
[zlink]$ python test/python/server.py
```

in a different terminal start zlink REPL:

```bash
[zlink]$ boot dev
```

### Rock & Roll

```clojure
=> (require '[zlink.core :as z] '[jsonista.core :as json])

;; data is anything that can be converted to bytes, in this case some JSON
=> (def data (.getBytes "[\"answer to the ultimate question of life universe and everything\"]"))

=> (def talk (z/zpipe "tcp://localhost:5555"
                      (fn [r] (-> (String. r)
                                  json/read-value))))
```
```
=> (talk data)
["answer to the ultimate question of life universe and everything" 42]
```

that `42` was added by Python on the [other side](test/python/server.py#7).

## Silly Bench

```clojure
=> (time (dotimes [_ 9000] (talk data)))
"Elapsed time: 1022.295091 msecs"
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
