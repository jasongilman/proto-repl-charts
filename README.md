# proto-repl-charts package

[API Docs](http://jasongilman.github.io/proto-repl-charts/index.html)

Proto REPL Charts is an Atom plugin that extends [Proto REPL](https://github.com/jasongilman/proto-repl) and allows you to display tables and graphs of results from executed Clojure Code.

Execute this in Proto REPL:

```Clojure
(proto-repl-charts.charts/line-chart
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

(Not necessary for self hosted REPL. The dependency is already available.)

Add
[![Clojars Project](https://img.shields.io/clojars/v/proto-repl-charts.svg)](https://clojars.org/proto-repl-charts)
 to your dependencies in your `project.clj` file.

(Proto REPL comes with a default Clojure project. If you bring open a new Atom window for and start a REPL it will already have proto-repl-charts dependency loaded and available.)

### 2. [Start the REPL in Proto REPL](https://github.com/jasongilman/proto-repl#usage) and wait for it to come up.

### 3. Execute one of the functions in the `proto-repl-charts.<chart-type-ns>` namespace. See the examples below.

The chart functions are all of the form `(proto-repl-charts.<chart-type-ns>/<function-name> <tab-name> <series-map> <[options]>)`

* `chart-type-ns` - one of `canvas`, `charts`, `graph`, or `table`
* `function-name` - the name of the function to invoke
* `tab-name` - is the name of the chart to put in the Atom tab. Repeated execution will replace the chart in the tab with the matching name.
* `series-map` - should be a map of series names to a sequence of values for that series. For example `{"alpha" [1 2 3], "beta" [2 4 5]}` would display two series named alpha and beta with the values 1, 2, and 3 for alpha and 2, 4, and 5 for beta.
* `options` - an optional map of display options. The only option supported right now is `:labels` which is a list of labels. The index of the label in the list corresponds to the index of the values in the series.

### Displaying a Line Chart

```Clojure
(let [input-values (range 0.0 6.0 0.5)]
  (proto-repl-charts.charts/line-chart
   "Trigonometry"
   {"sin" (map #(Math/sin %) input-values)
    "cos" (map #(Math/cos %) input-values)}
   {:labels input-values}))
```

![line chart](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/line_chart.png)

### Displaying a Bar Chart

```Clojure
(proto-repl-charts.charts/bar-chart
  "GDP_By_Year"
  {"2013" [16768 9469 4919 3731]
   "2014" [17418 10380 4616 3859]}
  {:labels ["US" "China" "Japan" "Germany"]})
```

![bar chart](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/bar_chart.png)

### Displaying a Scatter Chart

```Clojure
(let [tlr (java.util.concurrent.ThreadLocalRandom/current)]
  (proto-repl-charts.charts/scatter-chart
   "Randoms"
   {:gaussian (repeatedly 200 #(.nextGaussian tlr))
    :uniform  (repeatedly 200 #(.nextDouble tlr))}))
```

![scatter chart](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/scatter_chart.png)

### Displaying a Custom Chart

Displays a custom chart in a tab with the given name. [C3](http://c3js.org/) is the charting library used. The chart config will be converted from Clojure to a JavaScript object and passed to C3. It can be any configuration data C3 supports. See [C3 examples](http://c3js.org/examples.html) for more.

```Clojure
(proto-repl-charts.charts/custom-chart
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
(proto-repl-charts.table/table
  "Users"
  [{:name "Jane" :age 24 :favorite-color :blue}
   {:name "Matt" :age 28 :favorite-color :red}
   {:name "Austin" :age 56 :favorite-color :green}
   {:name "Lisa" :age 32 :favorite-color :green}
   {:name "Peter" :age 32 :favorite-color :green}])
```

![table from maps](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/table_from_maps.png)

### Displaying a Graph

Graphs of networks of nodes and edges can be displayed using the `proto-repl-charts.graph/graph` function.

See [Graphs](https://github.com/jasongilman/proto-repl-charts/blob/master/graphs.md) for details.

![map graph](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/map_graph.png)

### Drawing on a Canvas

Proto REPL Charts supports building more complex visualizations by drawing on an HTML canvas embedded within Atom using the `proto-repl-charts.canvas/draw` function.

See [Canvas](https://github.com/jasongilman/proto-repl-charts/blob/master/canvas.md) for details.

![Canvas Dragon Curve](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/canvas_dragon.png)
