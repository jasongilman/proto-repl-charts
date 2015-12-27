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

    constructor: (@name, data) ->
      super
      @showLoading()
      @createChart()

    serialize: ->
      # TODO how should we do serialization
      deserializer : 'ChartView'

    # TODO is this required?  Get rid of it
    renderHTML: ->
      @showLoading()

    display: (data)->
      if @chart
        @chart = @chart.destroy()

      chartSpec =
        bindto: @chartDiv
        data: data

      @chart = c3.generate(chartSpec)

    createChart: (data)->
      @chartDiv = document.createElement("div")
      @html $ @chartDiv
      @display(data)

    getTitle: ->
      @name

    # TODO is this required? Get rid of duplication
    getURI: ->
      "#{PROTOCOL}//chart/#{@name}"

    showLoading: ->
      @html $$$ ->
        @div class: 'atom-html-spinner', 'Loading your visualization\u2026'
