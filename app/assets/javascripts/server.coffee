class ServerHandler
  constructor: (@loader) ->
    @prompt = $('#prompt')
    @result = $('#result')
    @repl = $('#repl')
    @page = $('#text')
  onPageSuccess: (result) =>
    @process()
    @page.fadeOut('slow', () => @onFaded(result))
    @repl.scrollTop 999999 # FIXME
  onCommandSuccess: (result) =>
    @process()
    lines = result.split /\n/
    @addToResult line for line in lines
    @repl.scrollTop 999999 # FIXME
  onError: (error) ->
    @loader.hide()
    console.log error
    alert error
  # below are meant to be local methods
  onFaded: (result) ->
    @page.empty()
    @page.append(result)
    @page.fadeIn()
  process: ->
    prev = $('<div class="prev"/>')
    prev.text '> ' + @prompt.val()
    @result.append prev
    @prompt.prop 'disabled', false
    @prompt.val ''
    @loader.hide()
  addToResult: (str) ->
    line = $('<div/>')
    line.text str
    @result.append line

window.ServerHandler = ServerHandler
