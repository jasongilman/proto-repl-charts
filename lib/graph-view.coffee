{$, $$$, ScrollView}  = require 'atom-space-pen-views'
vis = require 'vis'

PROTOCOL = "proto-repl-charts:"

module.exports =
  class GraphView extends ScrollView
    name: null
    network: null
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
      if @network
        @network = @network.destroy()
      else
        @graphDiv = document.createElement("div")
        @html $ @graphDiv

      # create an array with nodes
      # nodes should be objects with id and label
      nodes = new vis.DataSet(data.nodes)

      #create an array with edges
      # Edges should be objects from and to
      edges = new vis.DataSet(data.edges)

      #create a network
      graphData =
        nodes: nodes,
        edges: edges
      options = data.options || {}
        # configure: 'nodes,edges'
      @network = new vis.Network(@graphDiv, graphData, options);

    # Redraws the graph
    redraw: ->
      # No redraw is needed
      null

    getTitle: ->
      @name

    showLoading: ->
      @html $$$ ->
        @div class: 'atom-html-spinner', 'Loading your visualization\u2026'
