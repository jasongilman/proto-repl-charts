(ns prc
  "Deprecated. Use namespaces that are children of proto-repl-charts."
  (:require [proto-repl-charts.graph :as g]
            [proto-repl-charts.table :as t]
            [proto-repl-charts.charts :as c]))

;; TODO retest everything here.

(defn graph
  "Deprecated. See proto-repl-charts.graph/graph"
  ([name graph-data]
   (g/graph name graph-data))
  ([name graph-data options]
   (g/graph name graph-data options)))

(defn table
  "Deprecated. See proto-repl-charts.table/table"
  [name rows]
  (t/table name rows))

(defn custom-chart
  "Deprecated. See proto-repl-charts.charts/custom-chart"
  [name chart-config]
  (c/custom-chart name chart-config))

(defn line-chart
  "Deprecated. See proto-repl-charts.charts/line-chart"
  ([name series-map]
   (c/line-chart name series-map))
  ([name series-map options]
   (c/line-chart name series-map options)))

(defn bar-chart
  "Deprecated. See proto-repl-charts.charts/bar-chart"
  ([name series-map]
   (c/bar-chart name series-map))
  ([name series-map options]
   (c/bar-chart name series-map options)))

(defn scatter-chart
  "Deprecated. See proto-repl-charts.charts/scatter-chart"
  ([name series-map]
   (c/scatter-chart name series-map))
  ([name series-map options]
   (c/scatter-chart name series-map options)))
