class LoadIndicator
  constructor: () ->
    @placeholder = $('#placeholder')
    @rotation = ['/', '-', '\\', '|']
    @i = 0
  show: () ->
    @f = window.setInterval(@setElement, 200)
  hide: () ->
    window.clearInterval @f
    @placeholder.text ">"
  setElement: () =>
    @placeholder.text @rotation[@i++]
    @i = 0 if @i == @rotation.length

window.LoadIndicator = LoadIndicator
