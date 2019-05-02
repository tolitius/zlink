(ns zlink.core
  (:require [clojure.tools.logging :as log]
            [jsonista.core :as json]
            [zlink.pipe.zmq :as zmq]
            [zlink.pipe.zhelpers :as mq]))

(defn spipe [host port]
  (zmq/zsocket (str "tcp://" host ":" port)))

(defn isocket [ctx to]
  (-> (mq/socket ctx mq/pair)
      (mq/connect (str "inproc://" to))))

(defn round-robin [n limit]
  (if (< n (dec limit))
    (inc n)
    0))

(defn a-pipe
  "a pipe to an asyncronous server. hence an mq/dealer since it carries identity"
  [{:keys [host port consume]}]
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
    (fn [^bytes data]
      (mq/send-bytes socket data)
      (-> (mq/recv-bytes socket)
          consume))))

(defn talking-pipe [ctx iname {:keys [host port]}]
  (let [isocket (-> (mq/socket ctx mq/pair)
                    (mq/bind (str "inproc://" iname)))
        socket (-> (mq/socket ctx mq/req)
                   (mq/connect (str "tcp://" host ":" port)))]
    (while true    ;; TODO: add a shutdown trigger / hook to cleanup
      (let [msg (mq/recv-bytes isocket)]
        (mq/send-bytes socket msg)
        (->> (mq/recv-bytes socket)
             (mq/send-bytes isocket))))))

(defn talking-pipes [{:keys [host start-port pipe-num consume]}]
  (let [current-pipe (atom 0)
        ctx mq/single-context
        pipes (mapv #(let [port (+ start-port %)]
                       (future (talking-pipe ctx port {:host host :port port}))
                       port)
                    (range pipe-num))
        next-port #(get pipes (swap! current-pipe round-robin pipe-num))]
    (fn [data]
      (future
        (let [pipe (isocket ctx (next-port))
              _ (mq/send-bytes pipe data)
              reply (-> (mq/recv-bytes pipe)
                        consume)]
          (mq/close pipe)
          reply)))))

(defn chat-once [ctx to ^bytes msg consume]
  (let [isocket (-> (mq/socket ctx mq/pair)
                    (mq/connect (str "inproc://" to)))
        _ (mq/send-bytes isocket msg)
        reply (-> (mq/recv-bytes isocket)
                  consume)]
    (mq/close isocket)
    reply))

(defn json-in [^bytes bs]
  (-> (String. bs)
      json/read-value))

(comment

;; via PAIR sockets
(def ctx mq/single-context)
(future (talking-pipe ctx "firefly:5555" {:host "localhost" :port 5555}))
(chat-once ctx "firefly" data json-in)
)

