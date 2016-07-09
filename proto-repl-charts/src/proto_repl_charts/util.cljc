(ns proto-repl-charts.util
  (require [clojure.string :as str]))

(defn error
  "Throws an exception containing a message joined from the msg-parts."
  [& msg-parts]
  (let [msg (str/join " " msg-parts)
        ex #?(:clj (Exception. msg)
              :cljs (js/Error msg))]
   (throw ex)))
