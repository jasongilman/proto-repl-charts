(ns proto-repl-charts.table)

(defn error
  [msg]
  #?(:clj (Exception. msg)
     :cljs (js/Error msg)))

(defn- table-input->matrix
  "Converts table input into a sequence of sequences. Assumes table input is
  either already a sequence a sequences or a sequence of maps. Throws an exception
  otherwise."
  [table-input]
  (if (sequential? table-input)
    (if (map? (first table-input))
      (let [cols (keys (first table-input))]
        (cons cols (map #(map % cols) table-input)))
      table-input)
    (throw (error
            "Table input must be a sequence of sequences or a sequence of maps."))))

(defn table
  "Displays the data in a table in a tab with the given name. rows can either be
  a sequence of sequences or a sequence of maps."
  [name rows]
  [:proto-repl-code-execution-extension
   "proto-repl-charts"
   {:type "table"
    :name name
    :data (table-input->matrix rows)}])
