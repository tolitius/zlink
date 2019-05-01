;; The file is adapted from zilch, with some other useful helper methods.
;; https://github.com/dysinger/zilch

(ns zlink.pipe.zhelpers
  (:refer-clojure :exclude [send])
  (:import [org.zeromq ZContext ZMQ ZMQ$Context ZMQ$Poller ZMQ$Socket ZMQQueue]))

(defonce single-context
  (ZMQ/context 1))

(defn context [threads]
  (ZMQ/context threads))

(def router ZMQ/XREP)
(def dealer ZMQ/XREQ)
(def req ZMQ/REQ)
(def rep ZMQ/REP)
(def xreq ZMQ/XREQ)
(def xrep ZMQ/XREP)
(def pub ZMQ/PUB)
(def sub ZMQ/SUB)
(def pair ZMQ/PAIR)
(def push ZMQ/PUSH)
(def pull ZMQ/PULL)

(def ^:const poller-types
  {:pollin  ZMQ$Poller/POLLIN
   :pollout ZMQ$Poller/POLLOUT
   :pollerr ZMQ$Poller/POLLERR})

(defn socket
  [#^ZMQ$Context context type]
  (.socket context type))

(defn queue
  [#^ZMQ$Context context #^ZMQ$Socket frontend #^ZMQ$Socket backend]
  (ZMQQueue. context frontend backend))

(defn bind
  [#^ZMQ$Socket socket url]
  (doto socket
    (.bind url)))

(defn connect
  [#^ZMQ$Socket socket url]
  (doto socket
    (.connect url)))

(defn subscribe
  ([#^ZMQ$Socket socket #^String topic]
     (doto socket
       (.subscribe (.getBytes topic))))
  ([#^ZMQ$Socket socket]
     (subscribe socket "")))

(defn send-bytes [^ZMQ$Socket socket ^bytes message]
  (.send socket message 0))

(defn recv-bytes [^ZMQ$Socket socket]
  (.recv socket 0))

(defn send-str
  ([^ZMQ$Socket socket ^String message]
   (.send socket (.getBytes message) 0))
  ([^ZMQ$Socket socket ^String message flags]
   (.send socket (.getBytes message) (int flags))))

(defn receive-str
  ([^ZMQ$Socket socket]
   (when-let [^bytes data (.recv socket 0)]
     (String. data)))
  ([^ZMQ$Socket socket flags]
   (when-let [^bytes data (.recv socket (int flags))]
     (String. data))))

(defn ^ZMQ$Socket set-identity
  [^ZMQ$Socket socket ^bytes identity]
  (.setIdentity socket identity)
  socket)

(defn register [^ZMQ$Poller poller ^ZMQ$Socket socket & events]
  (.register poller socket (int (apply bit-or 0 (keep poller-types events)))))

(defn unregister [^ZMQ$Poller poller ^ZMQ$Socket socket]
  (.unregister poller socket))

(defn poll
  ([^ZMQ$Poller poller]
   (.poll poller))
  ([^ZMQ$Poller poller ^long timeout]
   (.poll poller timeout)))

(defn check-poller
  [^ZMQ$Poller poller index type]
  (case type
    :pollin (.pollin poller (int index))
    :pollout (.pollout poller (int index))
    :pollerr (.pollerr poller (int index))))

(defmulti poller
  (fn [x & xs] (class x)))

(defmethod poller ZMQ$Context
  ([^ZMQ$Context ctx]
   (.poller ctx 1))
  ([^ZMQ$Context ctx size]
   (.poller ctx (int size))))

(defmethod poller ZContext
  ([^ZContext zctx]
   (.poller ^ZMQ$Context (.getContext zctx) 1))
  ([^ZContext zctx size]
   (.poller ^ZMQ$Context (.getContext zctx) (int size))))
