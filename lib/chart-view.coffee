{$, $$$, ScrollView}  = require 'atom-space-pen-views'
c3 = require 'c3'

PROTOCOL = "proto-repl-charts:"

module.exports =
  class ChartView extends ScrollView
    name: null
    chart: null
    chartDiv: null

    atom.deserializers.add(this)

    @deserialize: (state) ->
      new ChartView(state.name)

    @content: ->
      @div class: 'proto-repl-charts-chart native-key-bindings', tabindex: -1

    constructor: (@name) ->
      super
      @showLoading()

    serialize: ->
      # TODO how should we do serialization
      deserializer : 'ChartView'

    display: (data)->
      if @chart
        @chart = @chart.destroy()
      else
        @chartDiv = document.createElement("div")
        @html $ @chartDiv

      chartSpec =
        bindto: @chartDiv
        data: data

      @chart = c3.generate(chartSpec)

    # Redraws the chart
    redraw: ->
      @chart?.flush()

    getTitle: ->
      @name

    showLoading: ->
      @html $$$ ->
        @div class: 'atom-html-spinner', 'Loading your visualization\u2026'
