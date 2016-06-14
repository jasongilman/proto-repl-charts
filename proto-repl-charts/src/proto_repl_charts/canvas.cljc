(ns proto-repl-charts.canvas
  "Defines functions for opening and drawing to an HTML canvas in Atom. See the
   draw function for details."
  (require [proto-repl.extension-comm :as c]))

;; TODO document how to use it in README.


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Functions that generate data that can be drawn.

(defn clear-rect
  "Returns a command to clear the display."
  ([]
   (clear-rect 0 0 4000 4000))
  ([x y height width]
   [[:clearRect x y height width]]))

(defn stroke-rect
  "Returns a command set to draw the outline of a rectangle."
  [x y height width]
  [[:strokeRect x y height width]])

(defn fill-rect
  "Returns a command set to draw a filled in rectangle."
  [x y height width]
  [[:fillRect x y height width]])

(defn line
  "Returns a command set to draw a line between the two points."
  [x1 y1 x2 y2]
  [[:beginPath]
   [:moveTo x1 y1]
   [:lineTo x2 y2]
   [:stroke]])

(defn stroke-circle
  "Returns a command set to draw the outline of a circle."
  [x y radius]
  [[:beginPath]
   [:arc x y radius 0 (* 2 Math/PI) false]
   [:stroke]])

(defn fill-circle
  "Returns a command set to draw a filled in circle."
  [x y radius]
  [[:beginPath]
   [:arc x y radius 0 (* 2 Math/PI) false]
   [:fill]
   [:stroke]])

(defn text
  "Creates a command set to draw some text at the given point."
  [string x y]
  [[:fillText string x y]])

(defn line-width
  "Returns a command set to set the line width. Pass to the draw command to apply
   to future commands."
  [w]
  [[:set :lineWidth w]])

(defn font
  "Returns a command to set the font. Example \"10px sans-serif\" Pass to the
   draw command to apply to future commands."
  [str]
  [[:set :font str]])

(defn text-align
  "Returns a command to set the text alignment. Valid values 'start', 'end',
   'left', 'right' or 'center'. Pass to the draw command to apply to future
   commands."
  [str]
  [[:set :textAlign str]])

(defn fill-style
  "Returns a command to set the fill style such as the color. Example 'gray' or
  '#808080'. See https://developer.mozilla.org/en-US/docs/Web/CSS/color_value
   Pass to the draw command to apply to future commands."
  [str]
  [[:set :fillStyle str]])

(defn stroke-style
  "Returns a command to set the stroke style such as the color. Example 'gray' or
  '#808080'. See https://developer.mozilla.org/en-US/docs/Web/CSS/color_value
   Pass to the draw command to apply to future commands."
  [str]
  [[:set :strokeStyle str]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Side effecting drawing functions.

(defn- partition-command-sets
  "Partitions the sets of commands into evenly sized sets so that too much data
   won't be sent at any one time."
  [command-sets]
  (partition-all 100 (apply concat command-sets)))

(defn draw
  "Opens a new canvas with the given name or updates an existing canvas with the
   given name. `draw` is a wrapper over the JavaScript API for the canvas 2d
   context.

   ## High Level API

   Draw takes a sequence of commands that instruct it what to draw. There are
   functions in this namespace that return predefined commands to draw simple
   objects like lines, rectangles, and circles.

   This code would draw a thick black line over top of an outlined red circle.

   (draw \"Black Line and Red Circle\"
         [(clear-rect)
          (stroke-style \"red\")
          (line-width 1)
          (stroke-circle 200 300 70)

          (stroke-style \"black\")
          (line-width 5)
          (line 100 200 400 400)])

   ## Low Level API

   Draw takes a sequence of command sets. A command set is a sequence of commands.
   A command is a vector containing the keyword identifying the function to call
   on the Canvas 2d context along with the arguments that function takes.

   An example command is `[:fillRect 10 10 100 100]` which is equivalent to the
   JavaScript `ctx.fillRect(10, 10, 100, 100);` This would draw a filled in
   rectangle from the upper left point 10,10 that's 100 pixels tall and 100
   pixels wide.

   The following link is a good reference for the functions available on the 2d
   context: https://developer.mozilla.org/en-US/docs/Web/API/CanvasRenderingContext2D

   Example:

    This code will draw a line from the point 1,1 to 100,100

    (draw \"Simple Line\")
     [[[:beginPath]
       [:moveTo 1 1]
       [:lineTo 100 100]
       [:stroke]]])

    That's equivalent to this JavaScript code.

    var ctx = canvas.getContext('2d');
    ctx.beginPath();
    ctx.moveTo(1,1);
    ctx.lineTo(100,100);
    ctx.stroke();

   ### Setting Values
   Setting values on the context can be done via the :set command.

   [:set :lineWidth 5] is equivalent to ctx.lineWidth = 5;"
  ([name]
   (draw name []))
  ([name command-sets]
   (doseq [command-set (partition-command-sets command-sets)]
     (c/send-command
      "proto-repl-charts"
      {:type "canvas"
       :name name
       :data command-set}))))

(defn clear
  "Clears the canvas with the given name."
  [name]
  (draw name [(clear-rect)]))

(defn- request
  "Helper for fetching a value of something."
  [name command]
  (c/send-command
   "proto-repl-charts"
   {:type "canvas"
    :name name
    :data [command]}
   {:wait-for-response? true}))

(defn width
  "Returns the width of the named canvas."
  [name]
  (request name [:width]))

(defn height
  "Returns the height of the named canvas."
  [name]
  (request name [:height]))
