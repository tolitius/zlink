(ns zlink.core
  (:require [clojure.tools.logging :as log]
            [zlink.pipe.zmq :as zmq]
            [zlink.pipe.zhelpers :as mq]))

(defn zpipe [pipe-url consume]
  (let [;; thread-id (.getId (Thread/currentThread))
        ;; pipe-url (str pipe-url ":" thread-id)
        socket (zmq/zsocket pipe-url)]
    (fn [data]
      (mq/send-bytes socket data)
      (-> (mq/recv-bytes socket)
          consume))))

