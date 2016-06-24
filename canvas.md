# Proto REPL Charts Canvas

Proto REPL Charts supports building more complex visualizations by drawing on an HTML canvas embedded withing Atom.

## proto-repl-charts.canvas draw Function

The draw function is the main interaction point for drawing. It opens a new canvas with the given name or updates an existing canvas with the same name. `draw` is a wrapper over the JavaScript API for the canvas 2d context.

### High Level API

Draw takes a sequence of commands that instruct it what to draw. There are functions in this namespace that return predefined commands to draw simple objects like lines, rectangles, and circles.

This code would draw a thick black line over top of an outlined red circle.

```Clojure
(require '[proto-repl-charts.canvas :refer :all])

(draw "Black Line and Red Circle"
     [(clear-rect)
      (stroke-style "red")
      (line-width 1)
      (stroke-circle 200 300 70)

      (stroke-style "black")
      (line-width 5)
      (line 100 200 400 400)])
```

### Low Level API

Draw takes a sequence of command sets. A command set is a sequence of commands. A command is a vector containing the keyword identifying the function to call on the Canvas 2d context along with the arguments that function takes.

An example command is `[:fillRect 10 10 100 100]` which is equivalent to the JavaScript `ctx.fillRect(10, 10, 100, 100);` This would draw a filled in rectangle from the upper left point 10,10 that's 100 pixels tall and 100 pixels wide.

The following link is a good reference for the functions available on the 2d context: https://developer.mozilla.org/en-US/docs/Web/API/CanvasRenderingContext2D

Example:

This code will draw a line from the point 1,1 to 100,100

```Clojure
(draw "Simple Line"
 [[[:beginPath]
   [:moveTo 1 1]
   [:lineTo 100 100]
   [:stroke]]])
```

That's equivalent to this JavaScript code.

```JavaScript
var ctx = canvas.getContext('2d');
ctx.beginPath();
ctx.moveTo(1,1);
ctx.lineTo(100,100);
ctx.stroke();
```

### Setting Values

Setting values on the context can be done via the :set command.

`[:set :lineWidth 5]` is equivalent to `ctx.lineWidth = 5;`


## Examples

TODO examples
