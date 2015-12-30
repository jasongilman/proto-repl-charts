(ns prc
  "Defines functions that allow the display of charts or tables.")

;; TODO handle table data as a vector of maps or a vector of vectors

(defn- table-input->matrix
  [table-input]
  (if (sequential? table-input)
    (if (map? (first table-input))
      (let [cols (keys (first table-input))]
        (cons cols (map #(map % cols) table-input)))
      table-input)
    (throw (Exception.
            "Table input must be a sequence of sequences or a sequence of maps."))))


(defn table
  ""
  [name data]
  [:proto-repl-code-execution-extension
   "proto-repl-charts"
   {:type "table"
    :name name
    :data (table-input->matrix data)}])

(defn custom-chart
  "TODO document this"
  [name chart-config]
  [:proto-repl-code-execution-extension
   "proto-repl-charts"
   {:type "chart"
    :name name
    :data chart-config}])

(defn- process-options
  "Processes the options modifying the chart as necessary."
  [chart options]
  (if (:labels options)
    (assoc-in chart [2 :data :axis :x]
              {:type "category" :categories (:labels options)})
    chart))

(defn line-chart
  "TODO document this"
  ([name series]
   (line-chart name series nil))
  ([name series options]
   (-> (custom-chart name {:data {:json series}})
       (process-options options))))

(defn bar-chart
  "TODO document this"
  ([name series]
   (bar-chart name series nil))
  ([name series options]
   (-> (custom-chart name {:data {:json series
                                  :type "bar"}})
       (process-options options))))

(defn scatter-chart
  "TODO document this"
  ([name series]
   (scatter-chart name series nil))
  ([name series options]
   (-> (custom-chart name {:data {:json series
                                  :type "scatter"}})
       (process-options options))))
