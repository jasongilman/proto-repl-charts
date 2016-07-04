# Proto REPL Charts Graphs

[API Docs](http://jasongilman.github.io/proto-repl-charts/proto-repl-charts.graph.html)

Graphs of networks of nodes and edges can be displayed using the `prc/graph` function. Graphs are displayed using [vis.js](http://visjs.org/) networks.

## Simple Maps of Nodes and Edges

A simple map of nodes and edges can be provided to describe a graph.

```Clojure
(proto-repl-charts.graph/graph
  "A Graph from a map"
  {:nodes [:a :b :c :d :e :f :g]
   :edges [[:d :b] [:b :c] [:d :c] [:e :f] [:f :g]
           [:d :a] [:f :c]]})
```

![map graph](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/map_graph.png)

## Loom Graphs (Clojure Only currently)

[Loom](https://github.com/aysylu/loom) graphs are supported as well.

```Clojure
(require '[loom.graph :as lg])
(let [graph (lg/graph [1 0] [2 0] [4 0] [3 4] [5 4] [10 4] [9 10] [11 10]
                      [15 10] [17 15] [15 16] [7 0] [6 7] [8 6])]
  (proto-repl-charts.graph/graph "Loom Graph" graph))
```

## Event Handling

Proto REPL Charts supports subscribing to graph events. The events supported are listed [here with visjs documentation](http://visjs.org/docs/network/#Events). Pass a map of event types and handler function names with the options map argument. The function names must be fully scoped with namespace. The function will be invoked with the nodes and edges involved in the event.

```Clojure
(ns user)

(defn handle-double-click
  [event-data]
  (println (pr-str event-data))))

(proto-repl-charts.graph/graph
  "A Graph from a map"
  {:nodes [:a :b :c]
   :edges [[:a :b] [:a :c]]}
  {:events {:doubleClick 'user/handle-double-click}})
```

Double clicking a node prints something like the following. Node click data includes the full data of the node that was clicked and the edges that connect to that node.

```Clojure
{:nodes [{:label "a", :id "a"}],
 :edges [{:from "a",
          :id "e2ca05c8-a5bf-4b3b-b8bc-fd619a6360e3",
          :to "b"}
         {:from "a",
          :id "bd2191cf-1aae-449a-b13f-bb12e4ebaa7c",
          :to "c"}]}
```


## Full vis.js Customization

The full customization power of vis.js is supported. See the [vis.js documentation](http://visjs.org/docs/network/) for information on [options](http://visjs.org/docs/network/), [nodes]( http://visjs.org/docs/network/nodes.html), or [edges](http://visjs.org/docs/network/edges.html).

This is a [groups example](http://visjs.org/examples/network/nodeStyles/groups.html)

```Clojure
;; Groups will be given unique colors
(let [nodes [{:id 0 :label "0" :group 0}
             {:id 1 :label "1" :group 0}
             {:id 2 :label "2" :group 0}
             {:id 3 :label "3" :group 1}
             {:id 4 :label "4" :group 1}
             {:id 5 :label "5" :group 1}
             {:id 6 :label "6" :group 2}
             {:id 7 :label "7" :group 2}
             {:id 8 :label "8" :group 2}
             {:id 9 :label "9" :group 3}
             {:id 10 :label "10" :group 3}
             {:id 11 :label "11" :group 3}
             {:id 12 :label "12" :group 4}
             {:id 13 :label "13" :group 4}
             {:id 14 :label "14" :group 4}
             {:id 15 :label "15" :group 5}
             {:id 16 :label "16" :group 5}
             {:id 17 :label "17" :group 5}
             {:id 18 :label "18" :group 6}
             {:id 19 :label "19" :group 6}
             {:id 20 :label "20" :group 6}
             {:id 21 :label "21" :group 7}
             {:id 22 :label "22" :group 7}
             {:id 23 :label "23" :group 7}
             {:id 24 :label "24" :group 8}
             {:id 25 :label "25" :group 8}
             {:id 26 :label "26" :group 8}
             {:id 27 :label "27" :group 9}
             {:id 28 :label "28" :group 9}
             {:id 29 :label "29" :group 9}]
      edges [[1 0] [2 0] [4 3] [5 4] [4 0] [7 6] [8 7] [7 0] [10 9] [11 10]
             [10 4] [13 12] [14 13] [13 0] [16 15] [17 15] [15 10] [19 18]
             [20 19] [19 4] [22 21] [23 22] [22 13] [25 24] [26 25] [25 7]
             [28 27] [29 28] [28 0]]]
  (proto-repl-charts.graph/graph
    "Custom Graph"
    {:nodes nodes :edges edges}
    ;; Options
    {:nodes {:shape "dot" :size 30 :font {:size 32 :color "#111111"}
             :borderWidth 2}
     :edges {:width 2}}))
```

![custom graph](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/custom_graph.png)
