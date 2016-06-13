(ns proto-repl-charts.canvas
  "TODO

  Note that the 2d context is stateful and setting certain things will change it
  until it's changed back.
  "
  (require [proto-repl.extension-comm :as c]))

;; TODO document how to use it in README.


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Functions that generate data that can be drawn.

(defn clear-rect
  "TODO"
  ([]
   (clear-rect 0 0 2000 2000))
  ([x y height width]
   [[:clearRect x y height width]]))

(defn stroke-rect
  "TODO"
  [x y height width]
  [[:strokeRect x y height width]])

(defn fill-rect
  "TODO"
  [x y height width]
  [[:fillRect x y height width]])

(defn line
  "TODO"
  [x1 y1 x2 y2]
  [[:beginPath]
   [:moveTo x1 y1]
   [:lineTo x2 y2]
   [:stroke]])

(defn stroke-circle
  [x y radius]
  [[:beginPath]
   [:arc x y radius 0 (* 2 Math/PI) false]
   [:stroke]])

(defn fill-circle
  [x y radius]
  [[:beginPath]
   [:arc x y radius 0 (* 2 Math/PI) false]
   [:fill]
   [:stroke]])

(defn text
  "Creates a command to draw some text at the given point."
  [string x y]
  [[:fillText string x y]])

(defn line-width
  "Returns a command to set the line width. Pass to the draw command to apply
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
  [[:set :fillStyle str]])



;; TODO what else is important?
;; circles
;; text
;; setting colors - fill style and stroke style
;; Get and Set functions that will document the special ways of setting values.


(comment
 (draw "person")

 (draw
  "person"
  [(clear-rect)
   (stroke-circle 300 100 40)
   (line 350 200 350 400)]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Side effecting drawing functions.

(defn- partition-command-sets
  "Partitions the sets of commands into evenly sized sets so that too much data
   won't be sent at any one time."
  [command-sets]
  (partition-all 100 (apply concat command-sets)))

(defn draw
  "Opens a new canvas with the given name."
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
  "TODO"
  [name]
  (draw name [(clear-rect)]))

(defn- request
  "TODO"
  [name command]
  (c/send-command
   "proto-repl-charts"
   {:type "canvas"
    :name name
    :data [command]}
   {:wait-for-response? true}))

(defn width
  "TODO"
  [name]
  (request name [:width]))

(defn height
  "TODO"
  [name]
  (request name [:height]))
