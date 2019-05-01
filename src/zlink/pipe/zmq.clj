(ns zlink.pipe.zmq
  (:import [org.zeromq ZMQ$Socket])
  (:require [clojure.tools.logging :as log]
            [zlink.pipe.zhelpers :as mq]))

(defn zsocket [queue]
  (let [sock (-> mq/single-context
                 (mq/socket mq/req)
                 (mq/connect queue))]
    (log/info "[zmq]: producer is bound to [" queue "], let's rock'n'roll")
    sock))
