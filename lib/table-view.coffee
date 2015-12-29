{$, $$$, ScrollView}  = require 'atom-space-pen-views'
ht = require './handsontable.full.js'

PROTOCOL = "proto-repl-charts:"

module.exports =
  class TableView extends ScrollView
    name: null
    table: null

    atom.deserializers.add(this)

    @deserialize: (state) ->
      new TableView(state.name)

    @content: ->
      @div class: 'proto-repl-chart-table native-key-bindings', tabindex: -1

    constructor: (@name) ->
      super
      @showLoading()

    serialize: ->
      # TODO how should we do serialization
      # It's not currently working.
      deserializer : 'TableView'

    display: (data)->
      if @table
        @table.loadData(data)
      else
        @createTable(data)

    # Redraws the table
    redraw: ->
      @table?.render()

    createTable: (data)->
      div = document.createElement("div")

      @html $ div
      @table = new Handsontable div,
        data: data
        minSpareRows: 1
        rowHeaders: true
        colHeaders: true

        # Allow a right click menu in the table
        contextMenu: true

        # Make the whole table read only
        readOnly: true

        # Don't support the fill handle.
        fillHandle: false

        # Stretch to fit the width
        stretchH: 'all'

        # Allow manual resizing of columns
        manualColumnResize: true
        manualRowResize: true

        # Allow manual freezing of columns
        manualColumnFreeze: true

        # Allow manual moving of columns
        manualColumnMove: true
        manualRowMove: true

        # Highlight the current row
        currentRowClassName: 'currentRow'
        currentColClassName: 'currentCol'

    getTitle: ->
      @name

    showLoading: ->
      @html $$$ ->
        @div class: 'atom-html-spinner', 'Loading your visualization\u2026'
