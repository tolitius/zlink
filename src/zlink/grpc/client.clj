(ns zlink.grpc.client
  (import [io.data.pipe PiperClient DataRequest]))

(defn piper [host port]
  (PiperClient. host port))

(defn request [xs]
  (.build (doto (DataRequest/newBuilder)
                (.setData xs))))

(defn pipe-it [pipe data]
  (-> (.blockingStub pipe)
      (.send data)
      .getData))
