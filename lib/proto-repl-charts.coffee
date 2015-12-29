TableView = require './table-view'
ChartView = require './chart-view'
{CompositeDisposable} = require 'atom'
url = require 'url'

PROTOCOL = "proto-repl-charts:"

module.exports = ProtoReplCharts =
  subscriptions: null

  # A map of views addressed by name.
  tableViewsByName: {}
  chartViewsByName: {}

  # A map of panes ids to disposables of observers watching for the pane to be resized.
  paneResizeObservers: {}

  # Boolean indicates if this extension has been registered with Proto REPL
  registeredExtension: false

  # TODO figure out what should happen if this package is activated before the proto repl package.
  # I think we might be able to resolve this by adding proto repl as a package dependency of this one.

  registerExtension: ->
    unless @registeredExtension
      if window.protoRepl
        protoRepl.registerCodeExecutionExtension("proto-repl-charts", (data)=>
          @display(data))
        @registeredExtension = true

  # Handles the item becoming active in the pane. If the item is a view we care
  # about we'll add an observer to the pane so we know when the pane is resized
  # to redraw the view.
  handleActivePaneItemChanged: (pane, item)->
    if pane
      if disposable = @paneResizeObservers[pane.id]
        disposable.dispose()
        @paneResizeObservers[pane.id] = null

      if item instanceof ChartView || item instanceof TableView
        item.redraw()
        disposable = pane.onDidChangeFlexScale =>
          item.redraw()
        @paneResizeObservers[pane.id] = disposable

  openNewView: (type, map, name, data)->
    previousActivePane = atom.workspace.getActivePane()
    atom.workspace.open("#{PROTOCOL}//#{type}/#{name}", split: 'right', searchAllPanes: true).done (view)=>
      console.log(view)
      map[name] = view
      view.renderHTML()
      view.display(data)
      previousActivePane.activate()
      pane = atom.workspace.paneForItem(view)
      @handleActivePaneItemChanged(pane, view)

  display: (data)->
    if data.type == "table"
      if view = @tableViewsByName[data.name]
        view.display(data.data)
      else
        @openNewView(data.type, @tableViewsByName, data.name, data.data)
    else if data.type == "chart"
      if view = @chartViewsByName[data.name]
        view.display(data.data)
      else
        @openNewView(data.type, @chartViewsByName, data.name, data.data)
    else
      console.log("Unexpected data: " + data)

  activate: (state) ->
    window.protoReplCharts = this
    @registerExtension()

    @subscriptions = new CompositeDisposable
    @subscriptions.add atom.commands.add 'atom-workspace', 'proto-repl-charts:toggle': => @toggle()

    # Adds an observer to any pane that is showing one of the views we care about
    # so that we'll resize the view when the pane changes size.
    atom.workspace.observeActivePaneItem (item)=>
      pane = atom.workspace.paneForItem(item)
      @handleActivePaneItemChanged(pane, item)

    atom.workspace.onDidDestroyPaneItem (event)=>
      item = event.item
      pane = event.pane
      if item instanceof ChartView
        @handleActivePaneItemChanged(pane, null)
        @chartViewsByName[item.name] = null

      if item instanceof TableView
        @handleActivePaneItemChanged(pane, null)
        @tableViewsByName[item.name] = null

    atom.workspace.addOpener (uriToOpen) ->
      try
        {protocol, host, pathname} = url.parse(uriToOpen)
      catch error
        console.log error
        return

      return unless protocol == PROTOCOL

      name = pathname.substr(1)
      if host == "chart"
        new ChartView(name)
      else if host == "table"
        new TableView(name)
      else
        console.log("Unexpected host #{host} in #{uriToOpen}")

  deactivate: ->
    @subscriptions.dispose()

  serialize: ->
    {}

  toggle: ->
    @registerExtension()
    console.log 'ProtoReplCharts was toggled!'
