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
      ## TODO
      if @network
        @network = @network.destroy()
      else
        @graphDiv = document.createElement("div")
        @html $ @graphDiv

      # create an array with nodes
      nodes = new vis.DataSet([
        {id: 1, label: 'Node 1'},
        {id: 2, label: 'Node 2'},
        {id: 3, label: 'Node 3'},
        {id: 4, label: 'Node 4'},
        {id: 5, label: 'Node 5'}
      ])

      #create an array with edges
      edges = new vis.DataSet([
        {from: 1, to: 3},
        {from: 1, to: 2},
        {from: 2, to: 4},
        {from: 2, to: 5}
      ])

      #create a network
      data =
        nodes: nodes,
        edges: edges
      options = {}
        # configure: 'nodes,edges'
      @network = new vis.Network(@graphDiv, data, options);
      # TODO temp
      window.graphView = this

    # Redraws the graph
    redraw: ->
      # No redraw is needed
      null

    getTitle: ->
      @name

    showLoading: ->
      @html $$$ ->
        @div class: 'atom-html-spinner', 'Loading your visualization\u2026'
