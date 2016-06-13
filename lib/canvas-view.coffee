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

    # TODO doc string
    display: (commands)->
      unless @canvas
        @parentDiv = this[0]
        @canvas = @parentDiv.children[0]
        window.theCanvasView = this
        @canvas.height = @parentDiv.clientHeight
        @canvas.width = @parentDiv.clientWidth
        @autoResizeCanvas()

      ctx = @canvas.getContext("2d")

      ## TODO document the special commands
      # Get, set, width, height

      lastResponse = null
      for command in commands
        fnName = command.shift()
        args = command
        # console.log "Calling #{fnName} with #{args}"

        # Special Handlers
        if fnName == "get"
          lastResponse = ctx[args[0]]
        else if fnName == "set"
          ctx[args[0]] = args[1]
        else if fnName == "width"
          lastResponse = @canvas.width
        else if fnName == "height"
          lastResponse = @canvas.height

        # Catch all
        else
          lastResponse = ctx[fnName].apply(ctx, args)
      lastResponse

    getTitle: ->
      @name
