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

(defn z-pipe [pipe-url consume]
  "retruns a simple, blocking, req/rep function to use in a single thread"
  "i.e. (z-pipe \"tcp://localhost:5555\"
                json-in)"
  (let [pipe (-> mq/single-context
				 (mq/socket mq/req)
				 (mq/connect pipe-url))]
    (fn [^bytes data]
      (mq/send-bytes pipe data)
      (-> (mq/recv-bytes pipe)
          consume))))

(defn wrap-in-inproc [ctx iname {:keys [host port] :as conn}]
  (log/info "[zlink]: connecting" iname "to" conn)
  (let [req (-> (mq/socket ctx mq/pull)
                (mq/connect (str "inproc://" iname "-to")))
        rep (-> (mq/socket ctx mq/push)
                (mq/connect (str "inproc://" iname "-from")))
        socket (-> (mq/socket ctx mq/req)
                   (mq/connect (str "tcp://" host ":" port)))]
    (while (not (.. Thread currentThread isInterrupted))       ;; TODO: add a shutdown trigger / hook to cleanup
      (let [msg (mq/recv-bytes req)]
        (mq/send-bytes socket msg)
        (->> (mq/recv-bytes socket)
             (mq/send-bytes rep))))))

(defn fan-out [fan pnum {:keys [port] :as conn}]
  (let [ctx mq/single-context]
    {:ctx ctx
     :to (str "inproc://" fan "-to")
     :from (str "inproc://" fan "-from")
     :pipes (mapv #(future (wrap-in-inproc ctx fan (merge conn {:port %})))
                  (take pnum (iterate inc port)))}))

(defn speaker [{:keys [ctx to]}]
  (-> (mq/socket ctx mq/push)
      (mq/bind to)))

(defn say [pipe data]
  (mq/send-bytes pipe data))

;; TODO: rewrite in zmq/poll
(defn listen [{:keys [ctx from]} consume]
  (let [run? (atom true)]
	(future
	  (let [pipe (-> (mq/socket ctx mq/pull)
					 (mq/bind from))]
		(while @run?
		  (let [message (mq/recv-bytes pipe)]
			(consume message)))))
	{:stop #(reset! run? false)}))


;; tools

(defn measure-async [pipe msg mnum counter]
  (time (do
		  (reset! counter 1)
		  (dotimes [_ mnum]
			(say pipe msg))
		  (while (< @counter mnum)))))

(defn json-in [^bytes bs]
  (-> (String. bs)
      json/read-value))


;; playground

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

;; TODO: rewrite this in the above fan out speak/listen style
(defn a-pipes [{:keys [host start-port pnum consume]}]
  (let [current-pipe (atom 0)
        pipes (mapv #(a-pipe {:host host :port (+ start-port %) :consume consume}) (range (inc pnum)))
        next-pipe #(get pipes (swap! current-pipe round-robin pnum))]
    (fn [data]
      (let [pipe (next-pipe)]
        (future (pipe data))))))


(comment

(require '[zlink.core :as z] '[jsonista.core :as json])
(def message (.getBytes "[\"answer to the ultimate question of life universe and everything\"]"))
(def fan (z/fan-out "deep-thought" 10 {:host "localhost" :port 5555}))
(def speaker (z/speaker fan))
(def counter (atom 0))
(def listener (z/listen fan #(do (z/json-in %) (swap! counter inc))))

(z/say speaker message)
;; => true
;; => [answer to the ultimate question of life universe and everything 42]

(z/measure-async speaker message 10 counter)

((:stop listener))
  )
