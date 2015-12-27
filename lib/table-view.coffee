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
      deserializer : 'TableView'

    # TODO is this required?  Get rid of it
    renderHTML: ->
      @showLoading()

    display: (data)->
      if @table
        @table.loadData(data)
      else
        @createTable(data)

    createTable: (data)->
      div = document.createElement("div")
      # TODO try adding the div after generating the chart. It might show the loading more
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

    # TODO is this required? Get rid of duplication
    getURI: ->
      "#{PROTOCOL}//table/#{@name}"

    showLoading: ->
      @html $$$ ->
        @div class: 'atom-html-spinner', 'Loading your visualization\u2026'
