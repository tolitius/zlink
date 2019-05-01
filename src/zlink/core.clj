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

(defn a-pipe [{:keys [host port consume]}]
  (let [ctx (mq/context 1)
        random (java.util.Random.)
        identity (format "%04X-%04X"
                         (.nextInt random 0x10000)
                         (.nextInt random 0x10000))
        pipe (doto (mq/socket ctx mq/dealer)
               (mq/set-identity (.getBytes identity))
               (mq/connect (str "tcp://" host ":" port)))]
    (fn [data]
      (mq/send-bytes pipe data)
      (-> (mq/recv-bytes pipe)
          consume))))

(defn a-pipes [{:keys [host start-port pnum consume]}]
  (let [current-pipe (atom 0)
        pipes (mapv #(a-pipe {:host host :port (+ start-port %) :consume consume}) (range (inc pnum)))
        next-pipe #(get pipes (swap! current-pipe round-robin pnum))]
    (fn [data]
      (let [pipe (next-pipe)]
        (future (pipe data))))))

(defn z-pipes [{:keys [host start-port pnum consume]}]
  (let [current-pipe (atom 0)
        pipes (mapv #(spipe host (+ start-port %)) (range (inc pnum)))
        next-pipe #(get pipes (swap! current-pipe round-robin pnum))]
    (fn [data]
      (let [pipe (next-pipe)]
        (future
          (mq/send-bytes pipe data)
          (-> (mq/recv-bytes pipe)
              consume))))))

(defn z-pipe [pipe-url consume]
  (let [;; thread-id (.getId (Thread/currentThread))
        ;; pipe-url (str pipe-url ":" thread-id)
        socket (zmq/zsocket pipe-url)]
    (fn [data]
      (mq/send-bytes socket data)
      (-> (mq/recv-bytes socket)
          consume))))

