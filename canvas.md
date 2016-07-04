# Proto REPL Charts Canvas

[API Docs](http://jasongilman.github.io/proto-repl-charts/proto-repl-charts.canvas.html)

Proto REPL Charts supports building more complex visualizations by drawing on an HTML canvas embedded withing Atom.

## Using this new feature

If you're already using Atom and Proto REPL make sure you do the following to take advantage of this feature:

* Upgrade to or install the latest version of proto-repl and proto-repl-charts in Atom.
* Update your project.clj dependencies:
  * [![Clojars Project](https://img.shields.io/clojars/v/proto-repl-charts.svg)](https://clojars.org/proto-repl-charts)
  * [![Clojars Project](https://img.shields.io/clojars/v/proto-repl.svg)](https://clojars.org/proto-repl)
* Restart Atom

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

### A Stick Figure

```Clojure
(require '[proto-repl-charts.canvas :as c])

(c/draw
  "person"
  [(c/clear-rect)
   (c/stroke-style "red")
   (c/fill-style "red")
   ;; Head
   (c/stroke-circle 300 100 40)
   ;; Body
   (c/line 300 140 300 240)
   ;; right arm
   (c/line 300 180 350 160)
   ;; left arm
   (c/line 300 180 250 160)
   ;; Right leg
   (c/line 300 240 350 280)
   ;; Left leg
   (c/line 300 240 250 280)

   ;; Speech bubble
   (c/font "20px sans-serif")
   (c/text "Hello World!" 100 100)])
```

![Canvas Stick Figure](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/canvas_stick_figure.png)

### Fractals with Lindenmayer Systems

See code in [src/proto_repl_charts/examples/canvas/l_system.clj](https://github.com/jasongilman/proto-repl-charts/blob/master/proto-repl-charts/src/proto_repl_charts/examples/canvas/l_system.clj)

#### Sierpinski Triangle

```Clojure
(require '[proto-repl-charts.examples.canvas.l-system :as f])

(f/draw-fractal
 {:canvas-name "sierpinski-triangle"
  :x 600.0
  :y 500.0
  :start-direction 180
  :start "a"
  :depth 9
  :rules {\a "b-a-b"
          \b "a+b+a"}
  :operations {\a #(f/forward % 1)
               \b #(f/forward % 1)
               \- #(f/rotate % (Math/toRadians -60.0))
               \+ #(f/rotate % (Math/toRadians 60.0))}})

```

![Canvas Sierpinski Triangle](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/canvas_sierpinski.png)

#### Dragon Curve

```Clojure
(require '[proto-repl-charts.examples.canvas.l-system :as f])

(f/draw-fractal
 {:canvas-name "dragon"
  :x 500.0
  :y 500.0
  :start-direction 270
  :start "FX"
  :depth 14
  :rules {\X "X+YF"
          \Y "FX-Y"}
  :operations {\X #(f/forward % 3)
               \Y #(f/forward % 3)
               \- #(f/rotate % (Math/toRadians -90.0))
               \+ #(f/rotate % (Math/toRadians 90.0))}})

```

![Canvas Dragon Curve](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/canvas_dragon.png)

#### Plant Like Fractal

```Clojure
(require '[proto-repl-charts.examples.canvas.l-system :as f])

(let [distance 5
     distance-chaos 0.75
     angle (Math/toRadians 22)
     neg-angle (* -1.0 angle)
     angle-chaos 0.01]
 (f/draw-fractal
  {:canvas-name "tree"
   :start-direction 90
   :x 300.0
   :y 700.0
   :start "F"
   :depth 5
   :rules {\F "C0FF-[C1-F+F+F]+[C2+F-F-F]"}
   :operations {\F #(f/forward % (f/value-with-chaos distance-chaos distance))
                \[ start-branch
                \] end-branch
                \- #(f/rotate % (* -1 (f/value-with-chaos angle-chaos angle)))
                \+ #(f/rotate % (f/value-with-chaos angle-chaos angle))}}))
```

![Canvas Plant](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/canvas_plant_like.png)

### Rotating a Polygon

See code in [src/proto_repl_charts/examples/canvas/rotating_polygon.clj](https://github.com/jasongilman/proto-repl-charts/blob/master/proto-repl-charts/src/proto_repl_charts/examples/canvas/rotating_polygon.clj])

The following code draws 11,200 triangles that are rotated around the origin with a radius that's varying using a sine function. The lines of the polygon are translucent so the overlapping lines create a pattern

```Clojure
(require '[proto-repl-charts.examples.canvas.rotating-polygon :as r])

(let [size 400]
  (r/draw-rotated-polygon
   {:canvas-name "rotation"
    :size (* size 2)
    :num-sides 3
    :polygon-size-fn (constantly (/ size 20.0))
    :radius-fn (fn [n]
                 (let [d (- (/ (- size 10.0) 2.0) (/ size 20.0))]
                   (- d
                      (Math/abs (* size (Math/sin (mod n 350)))))))
    :num-polygons 11200
    :rotation-amount (/ (* 2 Math/PI) 11200.0)
    :background-style "black"
    :polygon-style "rgba(0, 255, 255, 0.15)"}))

```

![Canvas Rotating Polygon](https://github.com/jasongilman/proto-repl-charts/raw/master/examples/canvas_polygon_rotation.png)
