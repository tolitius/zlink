(def +version+ "0.1.0")

(set-env!
  :source-paths #{"src"}
  :dependencies '[[org.clojure/clojure      "1.10.0"]
                  [org.clojure/tools.logging "0.4.1"]
                  [metosin/jsonista "0.2.2"]
                  [zmq/zmq "4.0.5"]

                  ;; boot clj
                  [boot/core                "2.8.3"           :scope "provided"]
                  [adzerk/bootlaces         "0.1.13"          :scope "test"]
                  [adzerk/boot-test         "1.0.6"           :scope "test"]
                  [tolitius/boot-check      "0.1.1"           :scope "test"]])

(require '[adzerk.bootlaces :refer :all]
         '[tolitius.boot-check :as check]
         '[adzerk.boot-test :as bt])

(bootlaces! +version+)

(deftask dev []
  (System/setProperty "java.library.path"
                      (str "native/:" (System/getenv "LD_LIBRARY_PATH")))
  (repl))

(deftask check-sources []
  (comp
    (check/with-bikeshed)
    (check/with-eastwood)
    (check/with-yagni)
    (check/with-kibit)))

(task-options!
  push {:ensure-branch nil}
  pom {:project     'tolitius/zlink
       :version     +version+
       :description "links data between languages"
       :url         "https://github.com/tolitius/zlink"
       :scm         {:url "https://github.com/tolitius/zlink"}
       :license     {"Eclipse Public License"
                     "http://www.eclipse.org/legal/epl-v10.html"}})
