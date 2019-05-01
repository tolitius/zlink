(ns zlink.core
  (:require [clojure.tools.logging :as log]
            [zlink.pipe.zmq :as zmq]
            [zlink.pipe.zhelpers :as mq]))

(defn spipe [host port]
  (zmq/zsocket (str "tcp://" host ":" port)))

(defn round-robin [n limit]
  (if (< n (dec limit))
    (inc n)
    0))

(defn zpipes [{:keys [host start-port pnum consume] :as p}]
  (let [current-pipe (atom 0)
        pipes (mapv #(spipe host (+ start-port %)) (range (inc pnum)))
        next-pipe #(get pipes (swap! current-pipe round-robin pnum))]
    (fn [data]
      (let [pipe (next-pipe)]
        (future
          (mq/send-bytes pipe data)
          (-> (mq/recv-bytes pipe)
              consume))))))

(defn zpipe [pipe-url consume]
  (let [;; thread-id (.getId (Thread/currentThread))
        ;; pipe-url (str pipe-url ":" thread-id)
        socket (zmq/zsocket pipe-url)]
    (fn [data]
      (mq/send-bytes socket data)
      (-> (mq/recv-bytes socket)
          consume))))

