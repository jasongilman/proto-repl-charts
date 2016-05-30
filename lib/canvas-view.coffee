{$, $$$, ScrollView}  = require 'atom-space-pen-views'

PROTOCOL = "proto-repl-charts:"

module.exports =
  # TODO document class
  class CanvasView extends ScrollView
    # TODO document fields
    name: null
    canvas: null
    parentDiv: null
    resizeInterval: null

    atom.deserializers.add(this)

    @deserialize: (state) ->
      new CanvasView(state.name)

    @content: ->
      @div class: 'proto-repl-charts-canvas native-key-bindings', tabindex: -1, =>
        @canvas()

    constructor: (name) ->
      @name = decodeURIComponent(name)
      super

    serialize: ->
      deserializer : 'CanvasView'

    deactivate: ->
      if @resizeInterval
        clearInterval(@resizeInterval)

    autoResizeCanvas: ->
      divHeight = @parentDiv.clientHeight
      divWidth = @parentDiv.clientWidth
      @resizeInterval = setInterval(
        ()=>
          if divHeight != @parentDiv.clientHeight ||
              divWidth != @parentDiv.clientWidth
            console.log("DIV was resized")
            divHeight = @parentDiv.clientHeight
            divWidth = @parentDiv.clientWidth

            # Check if we need to grow the canvas. We won't shrink it to avoid
            # unnecessarily cutting off images.
            if divHeight > @canvas.height ||
                divWidth > @canvas.width
              # Capture an image of the current state.
              canvasImg = new Image()
              canvasImg.src = @canvas.toDataURL("image/png")
              # Resize the canvas.
              @canvas.height = divHeight
              @canvas.width = divWidth
              # Draw the old image on the resized canvas.
              @canvas.getContext("2d").drawImage(canvasImg, 0, 0)
        1000)

    display: (commands)->
      unless @canvas
        @parentDiv = this[0]
        @canvas = @parentDiv.children[0]
        window.theCanvasView = this
        @canvas.height = @parentDiv.clientHeight
        @canvas.width = @parentDiv.clientWidth
        @autoResizeCanvas()

      ctx = @canvas.getContext("2d")

      for [fnName, args] in commands
        console.log "Calling #{fnName} with #{args}"
        ctx[fnName].apply(ctx, args)


    # Redraws the chart
    redraw: ->
      console.log("Resizing")
      if @canvas
        @canvas.style.height =  @parentDiv.clientHeight
        @canvas.style.width = @parentDiv.clientWidth
      else
        console.log("No canvas")

    getTitle: ->
      @name
