(ns user
  (:require [clojure.tools.namespace.repl :as tnr]
            [clojure.repl :refer :all]
            [prc]
            [proto]))

(defn start
  [])

(defn reset []
  (tnr/refresh :after 'user/start))

(println "proto-repl-charts dev/user.clj loaded.")
