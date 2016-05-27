{$, $$$, ScrollView}  = require 'atom-space-pen-views'

PROTOCOL = "proto-repl-charts:"

module.exports =
  class CanvasView extends ScrollView
    name: null

    atom.deserializers.add(this)

    @deserialize: (state) ->
      new CanvasView(state.name)

    @content: ->
      @div class: 'proto-repl-charts-canvas native-key-bindings', tabindex: -1

    constructor: (name) ->
      @name = decodeURIComponent(name)
      super
      @showLoading()

    serialize: ->
      deserializer : 'CanvasView'

    display: (commands)->
      unless @canvas
        @canvas = document.createElement("canvas")
        @html $ @canvas

      window.theCanvas = @canvas
      ctx = theCanvas.getContext("2d")

      for [fnName, args] in commands
        console.log "Calling #{fnName} with #{args}"
        ctx[fnName].apply(ctx, args)



    # Redraws the chart
    redraw: ->
      # TODO should this do something?
      null

    getTitle: ->
      @name

    showLoading: ->
      @html $$$ ->
        @div class: 'atom-html-spinner', 'Loading your visualization\u2026'
