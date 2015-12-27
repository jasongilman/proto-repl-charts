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

  # Boolean indicates if this extension has been registered with Proto REPL
  registeredExtension: false

  # TODO figure out what should happen if this package is activated before the proto repl package.
  # I think we might be able to resolve this by adding proto repl as a package dependency of this one.

  # TODO how do we handle a view being closed? We need to detect that and remove it from the views by name

  registerExtension: ->
    unless @registeredExtension
      if window.protoRepl
        protoRepl.registerCodeExecutionExtension("proto-repl-charts", (data)=>
          @display(data))
        @registeredExtension = true

  openNewView: (type, map, name, data)->
    previousActivePane = atom.workspace.getActivePane()
    atom.workspace.open("#{PROTOCOL}//#{type}/#{name}", split: 'right', searchAllPanes: true).done (view)=>
      console.log(view)
      map[name] = view
      view.renderHTML()
      view.display(data)
      previousActivePane.activate()


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
    @registerExtension()

    @subscriptions = new CompositeDisposable
    @subscriptions.add atom.commands.add 'atom-workspace', 'proto-repl-charts:toggle': => @toggle()

    atom.workspace.addOpener (uriToOpen) ->
      try
        {protocol, host, pathname} = url.parse(uriToOpen)
      catch error
        console.log error
        return

      console.log(protocol, host, pathname)

      return unless protocol == PROTOCOL

      if host == "chart"
        new ChartView(pathname)
      else if host == "table"
        new TableView(pathname)
      else
        console.log("Unexpected host #{host} in #{uriToOpen}")

  deactivate: ->
    @subscriptions.dispose()

  serialize: ->
    {}

  toggle: ->
    @registerExtension()
    console.log 'ProtoReplCharts was toggled!'
