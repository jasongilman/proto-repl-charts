(ns proto-repl-charts.graph
  "Contains functions for converting a graphs into display data. Displayed graphs
   are maps of nodes and edges. Nodes are maps with :id and :label. Edges are
   maps of :from and :to."
  (:require [clojure.string :as str]
            [proto-repl-charts.util :as u]))

(def ^:private expected-msg
  (str "Expecting loom graph or a map containing :nodes and :edges. Nodes can be "
       "strings or maps with any of the fields here: "
       "http://visjs.org/docs/network/nodes.html Edges can be 2 item sequences "
       "or maps containing any of the fields described here: "
       "http://visjs.org/docs/network/edges.html."))

(defn- nodes->display-data
  "Converts a sequence of nodes into nodes for display."
  [nodes]
  (when-not (or (set? nodes) (sequential? nodes))
    (u/error "Expected sequence of nodes." expected-msg))

  (if (map? (first nodes))
    nodes
    (mapv #(hash-map :id % :label %) nodes)))

(defn- edges->display-data
  "Converts a sequence of edges into edges for display."
  [edges]
  (when-not (or (set? edges) (sequential? edges))
    (u/error "Expected sequence of edges." expected-msg))
  (cond
    ;; Allows displaying a graph with no edges.
    (empty? edges)
    edges

    (map? (first edges))
    edges

    (sequential? (first edges))
    (mapv #(hash-map :from (first %) :to (second %)) edges)

    :else
    (u/error "Unexpected type for edges." (type (first edges)) expected-msg)))

(defn- map-graph->display-graph
  "Converts a graph passed in as a map to a display graph."
  [mg]
  (when-not (contains? mg :nodes)
    (u/error "Missing key :nodes." expected-msg))
  (when-not (contains? mg :edges)
    (u/error "Missing key :edges." expected-msg))
  (let [{:keys [nodes edges]} mg]
    {:nodes (nodes->display-data nodes)
     :edges (edges->display-data edges)}))

#?(:clj
   (require 'loom.graph))
#?(:clj
   (defn- loom-graph->display-graph
     "Converts a loom graph to a display graph."
     [g]
     (let [nodes (nodes->display-data (loom.graph/nodes g))
           edges (if (loom.graph/directed? g)
                   (edges->display-data (loom.graph/edges g))
                   ;; Non-directed graph. We don't want duplicate edges between nodes
                   ;; This will return edges twice for an undirected graph
                   (->> g loom.graph/edges (mapv sort) (into #{}) edges->display-data))]
      {:nodes nodes
       :edges edges}))
   :cljs (defn- loom-graph->display-graph [g] g))

(defn- loom-graph?
  [graph-data]
  #?(:clj (loom.graph/graph? graph-data)
     :cljs false))

(defn- convert-graph-data-for-display
  "Converts graph data into data for display by vis.js."
  [graph-data options]
  (cond
    (loom-graph? graph-data)
    (assoc (loom-graph->display-graph graph-data) :options options)

    (map? graph-data)
    (assoc (map-graph->display-graph graph-data) :options options)

    :else
    (u/error "Unexpected graph data for display of type" (type graph-data) expected-msg)))


(defn graph
  "Takes graph data representing nodes and edges and displays it in Atom using
   vis.js. (http://http://visjs.org/).
   Arguments:
   * name - The name to put in the tab title. Will replace an existing tag with
     the same name.
   * graph-data - Can be a loom graph or a map containing a sequence of nodes and
     edges. Nodes can be a sequence of identifiers (strings, numbers, keys) or
     can be a map containing data matching description here:
     http://visjs.org/docs/network/nodes.html. Edges can be a sequence of 2 item
     sequences or maps containing any of the fields described here:
     http://visjs.org/docs/network/edges.html
   * options - Optional map of visjs network options. See http://visjs.org/docs/network/"
  ([name graph-data]
   (graph name graph-data nil))
  ([name graph-data options]
   [:proto-repl-code-execution-extension
    "proto-repl-charts"
    {:type "graph"
     :name name
     :data (convert-graph-data-for-display graph-data options)}]))

;; Some graph examples.
(comment
 (graph "mygraph" {:nodes ["a" "b" "c"]
                   :edges [["a" "b"]
                           ["b" "c"]]})
 (do
  (require '[loom.graph :as lg])
  (let [loom-graph (lg/graph [1 2] [3 4] [1 4] [5 4] [3 5])]
    (graph "loom" loom-graph)))

 (do
  (require '[loom.graph :as lg])
  (require '[loom.gen :as gen])
  (let [loom-graph (lg/graph)
        loom-graph (gen/gen-rand loom-graph 200 200)]
    (graph "loom" loom-graph {:layout {:improvedLayout false}}))))
