(ns zlink.grpc.client
  (import [io.data.pipe PiperClient DataRequest]
          [io.grpc.stub StreamObserver]))

(defn piper [host port]
  (PiperClient. host port))

(defn request [xs]
  (.build (doto (DataRequest/newBuilder)
                (.setData xs))))

(defn pipe-it [pipe data]
  (-> (.blockingStub pipe)
      (.send data)
      .getData))

(defn stream-observer [{:keys [on-next on-error on-completed]
                        :or {on-next println
                             on-error #(throw (RuntimeException. %))
                             on-completed (fn [] :ok)}}]
  (reify StreamObserver
    (onNext [this event]
      (on-next event))
    (onError [this throwable]
      (on-error throwable))
    (onCompleted [this]
      (on-completed))))

(defn apipe-it [pipe data on-event]
  (-> (.asyncStub pipe)
      (.send data on-event)))


;; repl fun
(defn and-wait-for-all-async [pipes data times]
  (let [counter (atom 1)
        on-event (stream-observer {:on-next #(.getData %)
                                   :on-completed #(swap! counter inc)})
        time-shard (inc (/ times (count pipes)))]
    (doseq [pipe pipes]
      (future (dotimes [_ time-shard] (apipe-it pipe data on-event))))

    (while (< @counter times))
    @counter))
