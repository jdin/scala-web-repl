
$ ->
  $(document).keydown onKey
  prompt = $('#prompt')
  focusPrompt = () -> prompt.focus()
  window.setInterval( focusPrompt, 500 )

onKey = (event) ->
  prompt = $('#prompt')
  handler = new ServerHandler
  switch event.which
    when 13 # ENTER 
      command = prompt.val()
      history.save command
      prompt.prop 'disabled', true
      # TODO start waiting
      result = $.get 'exec', {cmd: command}
      result.success (r)-> handler.onSuccess(r)
      result.fail (e) -> handler.onError(e)
    when 38 # UP
      hist = history.getPrevCmd()
      prompt.val if hist then hist else ""
      event.preventDefault()
    when 40 # DOWN
      hist = history.getNextCmd()
      prompt.val if hist then hist else ""
      event.preventDefault()

class ServerHandler
  constructor: () ->
    @prompt = $('#prompt')
    @result = $('#result')
    @repl = $('#repl')
  onSuccess: (result) ->
    prev = $('<div class="prev"/>')
    prev.text '> ' + @prompt.val()
    @result.append prev
    lines = result.split /\n/
    @addToResult line for line in lines
    @prompt.prop 'disabled', false
    @prompt.val ''
    history.reset()
    @repl.scrollTop 999999 # FIXME
  onError: (error) ->
    console.log error
    alert error
  addToResult: (str) ->
    line = $('<div/>')
    line.text str
    @result.append line

class History
  constructor: () ->
    @pointer = -1
    @storageName = 'commandHistory'
  # reset pointer 
  reset: () -> @pointer = -1
  # return an array of commands
  # ordered, most recent on top
  # never null
  getAll: () ->
    hist = localStorage[@storageName]
    return if hist then hist.split(/\n/) else []
  # return previous command or null
  getPrevCmd: () ->
    hist = @getAll()
    @pointer++
    if @pointer < hist.length and @pointer >= 0
      return hist[@pointer]
    else
      @pointer-- if @pointer > hist.length
      return null
  # return next command or null
  getNextCmd: () ->
    @pointer--
    hist = @getAll()
    if @pointer < hist.length and @pointer >= 0
      return hist[@pointer]
    else
      @pointer++ if @pointer < -1
      return null
  # save command to local storage
  save: (cmd) ->
    lines = @getAll()
    lines.splice 0, 0, cmd
    localStorage[@storageName] = lines.reduce (x, y) -> x + '\n' + y
    
history = new History



