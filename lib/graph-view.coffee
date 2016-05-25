{$, $$$, ScrollView}  = require 'atom-space-pen-views'
vis = require 'vis'

PROTOCOL = "proto-repl-charts:"

module.exports =
  class GraphView extends ScrollView
    name: null
    graph2d: null
    graphDiv: null

    atom.deserializers.add(this)

    @deserialize: (state) ->
      new GraphView(state.name)

    @content: ->
      @div class: 'proto-repl-charts-graph native-key-bindings', tabindex: -1

    constructor: (name) ->
      @name = decodeURIComponent(name)
      super
      @showLoading()

    serialize: ->
      deserializer : 'GraphView'

    display: (data)->
      if @graph2d
        @graph2d = @graph2d.destroy()
      else
        @graphDiv = document.createElement("div")
        @html $ @graphDiv

      console.log data
      dataset = new vis.DataSet(data.items)

      @graph2d = new vis.Graph2d(@graphDiv, dataset, data.options);

    # Redraws the graph
    redraw: ->
      # No redraw is needed
      null

    getTitle: ->
      @name

    showLoading: ->
      @html $$$ ->
        @div class: 'atom-html-spinner', 'Loading your visualization\u2026'
