(ns proto-repl-charts.canvas
  "Defines functions for opening and drawing to an HTML canvas in Atom. See the
   draw function for details."
  (:require [proto-repl.extension-comm :as c]
           [proto-repl-charts.util :as u]))

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

(defn stroke-polygon
  "Returns a commands set to draw a polygon connecting the given points. points
   is a vector of tuples of x and y for each point."
  [points]
  (let [[start-x start-y] (first points)]
    (concat
     [[:beginPath]
      [:moveTo start-x start-y]]
     (for [[x y] (rest points)]
       [:lineTo x y])
     [[:closePath]
      [:stroke]])))

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
;; Functions for impacting the coordinate system through transformations

(defn transform-reset
  "Returns a command set to resets the transformations using the identity matrix"
  []
  [[:setTransform 1 0 0 1 0 0]])

(defn rotate
  "Returns a command set to add a rotation to the transformation matrix. angle
   is expressed in radians."
  [angle]
  [[:rotate angle]])

(defn scale
  "Returns a command set to add a scaling transformation to the canvas units by x
   horizontally and by y vertically. If one argument is provided they're both
   scaled evenly."
  ([size]
   (scale size size))
  ([x y]
   [[:scale x y]]))

(defn translate
  "Returns a command set to add a translation transformation by moving the canvas
   and its origin x horizontally and y vertically."
  [x y]
  [[:translate x y]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Side effecting drawing functions.

(def command-set-error-msg
  (str "Command set does not appear to be valid. Accepts command sets in the "
       "one of the following syntaxes:\n"
       "[:command 1 2 3] - a single command\n"
       "[[:command 1 2 3]] - a list of commands (a command set)\n"
       "[[[:command 1 2 3]]] - a list of command sets"))

(defn- clean-command-sets
  "Takes drawing commands in a variety of styles and converts it to a command set."
  [command-sets]
  (if (sequential? command-sets)
    (cond
      (keyword? (first command-sets))
      [[command-sets]]

      (keyword? (ffirst command-sets))
      [command-sets]

      (keyword? (first (ffirst command-sets)))
      command-sets

      :else
      (u/error command-set-error-msg))
    (u/error command-set-error-msg)))

(defn- valid-command-arg?
  "Returns true if the argument is valid for a command."
  [arg]
  (or (keyword? arg)
      (number? arg)
      (string? arg)
      (true? arg)
      (false? arg)
      (nil? arg)))

(defn- validate-command
  "Validates the command. If its valid then it returns the command. If it's
  invalid throws an error"
  [command]
  (if (and (sequential? command)
           (keyword? (first command))
           (every? valid-command-arg? (drop 1 command)))
    command
    (u/error "Received invalid command:"
             (pr-str command)
             (str "A command must be a sequence starting with a keyword and "
                  "followed by arguments of a keyword, number, string, boolean,"
                  " or nil. Example: [:command :one 2 \"three\"]"))))

(def partition-command-sets-xducer
  "A transducer that concatenates together command sets, validates them, and
   then splits the command set into sets of 100 commands to send at a time to
   GUI side for drawing."
  (comp
   cat
   (map validate-command)
   (partition-all 100)))

(defn- partition-command-sets
  "Partitions the sets of commands into evenly sized sets so that too much data
   won't be sent at any one time."
  [command-sets]
  (sequence partition-command-sets-xducer command-sets))

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

   Draw takes any of the following:

   * a command
   * a sequence of commands (also called a command set)
   * a sequence of command sets

   A command is a vector containing the keyword identifying the function to call
   on the Canvas 2d context along with the arguments that function takes. An
   example command is `[:fillRect 10 10 100 100]` which is equivalent to the
   JavaScript `ctx.fillRect(10, 10, 100, 100);` This would draw a filled in
   rectangle from the upper left point 10,10 that's 100 pixels tall and 100
   pixels wide.

   The following link is a good reference for the functions available on the 2d
   context: https://developer.mozilla.org/en-US/docs/Web/API/CanvasRenderingContext2D

   Example:

    This code will draw a line from the point 1,1 to 100,100

    (draw \"Simple Line\")
      [[:beginPath]
       [:moveTo 1 1]
       [:lineTo 100 100]
       [:stroke]])

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
   (doseq [command-set (partition-command-sets (clean-command-sets command-sets))]
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
  "Returns the width of the named canvas. Note the canvas must have been previously
   opened for this to work."
  [name]
  (request name [:width]))

(defn height
  "Returns the height of the named canvas. Note the canvas must have been previously
   opened for this to work."
  [name]
  (request name [:height]))
