# history navigation
class CmdHistory
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

window.CmdHistory = CmdHistory
