cmdHistory = new CmdHistory

$ ->
  $(document).keydown onKey
  prompt = $('#prompt')
  focusPrompt = () -> prompt.focus()
  window.setInterval( focusPrompt, 500 )

onKey = (event) ->
  prompt = $('#prompt')
  loader = new LoadIndicator
  handler = new ServerHandler loader
  switch event.which
    when 13 # ENTER 
      command = prompt.val()
      cmdHistory.save command
      cmdHistory.reset()
      prompt.prop 'disabled', true
      loader.show()
      if command == ':next' # show next page
        result = $.get 'nextLesson'
        result.success  handler.onPageSuccess
        result.fail (jqXHR, status, e) -> handler.onError(e)
      else # evaluate
        result = $.get 'exec', {cmd: command}
        result.success handler.onCommandSuccess
        result.fail (jqXHR, status, e) -> handler.onError(e)
    when 38 # UP
      hist = cmdHistory.getPrevCmd()
      prompt.val if hist then hist else ""
      event.preventDefault()
    when 40 # DOWN
      hist = cmdHistory.getNextCmd()
      prompt.val if hist then hist else ""
      event.preventDefault()

