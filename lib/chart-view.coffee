{$, $$$, ScrollView}  = require 'atom-space-pen-views'
c3 = require 'jg-c3'
Resizable = require('resizable')

PROTOCOL = "proto-repl-charts:"

module.exports =
  class ChartView extends ScrollView
    name: null
    chart: null
    chartDiv: null
    resizable: null

    atom.deserializers.add(this)

    @deserialize: (state) ->
      new ChartView(state.name)

    @content: ->
      @div class: 'proto-repl-charts-chart native-key-bindings', tabindex: -1

    constructor: (name) ->
      @name = decodeURIComponent(name)
      super
      @showLoading()

    serialize: ->
      deserializer : 'ChartView'

    display: (data)->
      if @chart
        @chart = @chart.destroy()
      else
        @chartDiv = document.createElement("div")
        @html $ @chartDiv

      data["bindto"] = @chartDiv

      @chart = c3.generate(data)

      # Allow resizing the chart area
      @resizable = new Resizable @chartDiv,
      	within: 'parent',
      	handles: 's',
      	threshold: 10

      @resizable.on 'resize', =>
        height = @chartDiv.style.height.replace("px","")
        @chart.resize
          height: new Number(height)

      # Style the handle
      handle = @resizable.handles.s
      handle.classList.add("icon")
      handle.classList.add("icon-three-bars")

    # Redraws the chart
    redraw: ->
      @chart?.flush()

    getTitle: ->
      @name

    showLoading: ->
      @html $$$ ->
        @div class: 'atom-html-spinner', 'Loading your visualization\u2026'
