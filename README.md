# proto-repl-charts package

Proto REPL Charts is an Atom plugin that extends [Proto REPL](https://github.com/jasongilman/proto-repl) and allows you to display tables and graphs of results from executed Clojure Code.

Execute this in Proto REPL:

```Clojure
(prc/line-chart
 "Trigonometry"
 {"sin" (map #(Math/sin %) (range 0.0 6.0 0.2))
  "cos" (map #(Math/cos %) (range 0.0 6.0 0.2))})
```

... and display this:

![A screenshot of Proto REPL Charts showing a graph with sine and cosine](https://github.com/jasongilman/proto-repl-charts/raw/master/front_image.png)

## Installation

1. Install [Proto REPL](https://github.com/jasongilman/proto-repl#installation).
2. `apm install proto-repl-charts` or go to your Atom settings, select "+ Install" and search for "proto-repl-charts".

## Usage

Proto REPL Charts are invoked from Clojure code run in Proto REPL. A very small Clojure library, proto-repl-charts, defines a namespace `prc` with functions for displaying different charts.

### 1. Add proto-repl-charts as a dependency in your Clojure project.

Add
[![Clojars Project](https://img.shields.io/clojars/v/proto-repl-charts.svg)](https://clojars.org/proto-repl-charts)
 to your dependencies in your `project.clj` file.

(Proto REPL comes with a default Clojure project. If you bring open a new Atom window for and start a REPL it will already have proto-repl-charts dependency loaded and available.)

### 2. [Start the REPL in Proto REPL](https://github.com/jasongilman/proto-repl#usage) and wait for it to come up.

### 3. Execute one of the functions in the `prc` namespace. See the examples below.

The `prc` chart functions are all of the form `(prc/<function-name> <tab-name> <series-map> <[options]>)`

* `function-name` - the name of the function to invoke
* `tab-name` - is the name of the chart to put in the Atom tab. Repeated execution will replace the chart in the tab with the matching name.
* `series-map` - should be a map of series names to a sequence of values for that series. For example `{"alpha" [1 2 3], "beta" [2 4 5]}` would display two series named alpha and beta with the values 1, 2, and 3 for alpha and 2, 4, and 5 for beta.
* `options` - an optional map of display options. The only option supported right now is `:labels` which is a list of labels. The index of the label in the list corresponds to the index of the values in the series.

### Displaying a Line Chart

```Clojure
(let [input-values (range 0.0 6.0 0.5)]
  (prc/line-chart
   "Trigonometry"
   {"sin" (map #(Math/sin %) input-values)
    "cos" (map #(Math/cos %) input-values)}
   {:labels input-values}))
```

![line chart](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/line_chart.png)

### Displaying a Bar Chart

```Clojure
(prc/bar-chart
  "GDP_By_Year"
  {"2013" [16768 9469 4919 3731]
   "2014" [17418 10380 4616 3859]}
  {:labels ["US" "China" "Japan" "Germany"]})
```

![bar chart](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/bar_chart.png)

### Displaying a Scatter Chart

```Clojure
(let [tlr (java.util.concurrent.ThreadLocalRandom/current)]
  (prc/scatter-chart
   "Randoms"
   {:gaussian (repeatedly 200 #(.nextGaussian tlr))
    :uniform  (repeatedly 200 #(.nextDouble tlr))}))
```

![scatter chart](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/scatter_chart.png)

### Displaying a Custom Chart

Displays a custom chart in a tab with the given name. [C3](http://c3js.org/) is the charting library used. The chart config will be converted from Clojure to a JavaScript object and passed to C3. It can be any configuration data C3 supports. See [C3 examples](http://c3js.org/examples.html) for more.

```Clojure
(prc/custom-chart
  "Custom"
  {:data {:columns
          [["data1" 30 20 50 40 60 50]
           ["data2" 200 130 90 240 130 220]
           ["data3" 300 200 160 400 250 250]
           ["data4" 200 130 90 240 130 220]
           ["data5" 130 120 150 140 160 150]
           ["data6" 90 70 20 50 60 120]]
          :type "bar"
          :types {:data3 "spline"
                  :data4 "line"
                  :data6 "area"}
          :groups [["data1" "data2"]]}})
```

![custom chart](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/custom_chart.png)

### Displaying a Table

Proto REPL Charts can display a table of data that can be sorted by individual columns. The row data can either be a sequence of sequences or a sequence of maps.

```Clojure
(prc/table
  "Users"
  [{:name "Jane" :age 24 :favorite-color :blue}
   {:name "Matt" :age 28 :favorite-color :red}
   {:name "Austin" :age 56 :favorite-color :green}
   {:name "Lisa" :age 32 :favorite-color :green}
   {:name "Peter" :age 32 :favorite-color :green}])
```

![table from maps](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/table_from_maps.png)

### Displaying a Graph

Graphs of networks of nodes and edges can be displayed using the `prc/graph` function. Graphs are displayed using [vis.js](http://visjs.org/) networks.

#### Simple Maps of Nodes and Edges

A simple map of nodes and edges can be provided to describe a graph.

```Clojure
(prc/graph "A Graph from a map"
           {:nodes [:a :b :c :d :e :f :g]
            :edges [[:d :b] [:b :c] [:d :c] [:e :f] [:f :g]
                    [:d :a] [:f :c]]})
```

![map graph](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/map_graph.png)

#### Loom Graphs

[Loom](https://github.com/aysylu/loom) graphs are supported as well.

```Clojure
(require '[loom.graph :as lg])
(let [graph (lg/graph [1 0] [2 0] [4 0] [3 4] [5 4] [10 4] [9 10] [11 10]
                      [15 10] [17 15] [15 16] [7 0] [6 7] [8 6])]
  (prc/graph "Loom Graph" graph))
```

#### Event Handling

Proto REPL Charts supports subscribing to graph events. The events supported are listed [here with visjs documentation](http://visjs.org/docs/network/#Events). Pass a map of event types and handler function names with the options map argument. The function names must be fully scoped with namespace. The function will be invoked with the nodes and edges involved in the event.

```Clojure
(defn handle-double-click
  [event-data]
  (println (pr-str event-data))))

(prc/graph "A Graph from a map"
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


#### Full vis.js Customization

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
  (prc/graph "Custom Graph"
             {:nodes nodes :edges edges}
             ;; Options
             {:nodes {:shape "dot" :size 30 :font {:size 32 :color "#111111"}
                      :borderWidth 2}
              :edges {:width 2}}))
```

![custom graph](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/custom_graph.png)
