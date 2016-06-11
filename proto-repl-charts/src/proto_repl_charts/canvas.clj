(ns proto-repl-charts.canvas)

(defn canvas
  "TODO"
  [name data]
  [:proto-repl-code-execution-extension
   "proto-repl-charts"
   {:type "canvas"
    :name name
    :data data}])
