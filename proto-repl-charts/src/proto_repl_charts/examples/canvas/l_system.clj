(ns proto-repl-charts.examples.canvas.l-system
  "This provides an example of drawing on the canvas using Lindenmayer Systems
   aka L-systems. Based on https://brehaut.net/blog/2011/l_systems"
  (require [proto-repl-charts.canvas :as c]))

(defn expand-l-system
  "Expands the sequence of items using the given production rules.
   * items - A sequence of elements to be expanded. Usually a string of characters.
   * production-rules - a function that takes one item from the sequence and return
     a sequence of new items that it expands to. Usually a map of characters to strings.
   * depth - The number of times to expand the items.

   Example:
   (expand-l-system {\\A \"AB\" \\B \"A\"} 3 \"A\")
   => (\\A \\B \\A \\A \\B)"
  [production-rules depth items]
  (if (< depth 1)
    items
    (mapcat (fn [item]
              (expand-l-system production-rules
                               (dec depth)
                               (or (production-rules item)
                                   ;; Return the item if there's no rule associated with it.
                                   [item])))
            items)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Turtle functions. These are used during the translation of an expanded L-system
;; to draw the fractal image.

(defn- new-turtle
  "Creates a new turtle with the given start position and direction. A turtle is
   is a map that contains a set of moves of where it has been, a location and
   direction of the turtle, and a stack of points for drawing branches."
  [start-x start-y start-direction]
  (transient
   {:direction (Math/toRadians (or start-direction 0.0))
    :curr-x start-x
    :curr-y start-y
    :stack []
    :moves (transient
            [[:beginPath]
             [:moveTo start-x start-y]])}))

(defn- finalize-moves
  "Finalizes the turtle moves. Returns the set of canvas drawing commands that
   will draw everywhere the turtle has been."
  [turtle]
  (persistent! (conj! (:moves turtle) [:stroke])))

(defn- run-turtle
  "Moves the turtle through it's different operations.
   * operations-map - a map of an item to a function for processing that item.
   The function should take a turtle and return the turtle in it's new state.
   * commands - The sequence of items expanded by the l-system."
  [turtle operations-map commands]
  (reduce (fn [t cmd]
            ((operations-map cmd identity) t))
          turtle
          commands))

(defn forward
  "Moves turtle forward the specified distance."
  [turtle ^double distance]
  (let [{:keys [^double direction
                ^double curr-x
                ^double curr-y
                moves]} turtle
        new-x (+ curr-x (* distance (Math/cos direction)))
        new-y (- curr-y (* distance (Math/sin direction)))]
    (assoc! turtle
            :curr-x new-x
            :curr-y new-y
            :moves (conj! moves [:lineTo (long new-x) (long new-y)]))))

(def ^:const ^:private ^:double TAU
  "2 PI - aka a complete circle in radians"
  (* 2.0 Math/PI))

(defn rotate
  "Rotates the turtle the specified number of radians"
  [turtle ^double theta]
  (assoc! turtle :direction (mod (+ ^double (:direction turtle) theta) TAU)))

(defn start-branch
  "Start a new branch on the turtle"
  [turtle]
  (let [{:keys [stack curr-x curr-y]} turtle]
    (assoc! turtle :stack (conj stack [curr-x curr-y]))))

(defn end-branch
  "Ends a branch for the turtle. Returns it back to it's last known location."
  [turtle]
  (let [{:keys [stack moves]} turtle
        [last-x last-y] (last stack)]
    (assoc! turtle
            :stack (pop stack)
            :curr-x last-x
            :curr-y last-y
            :moves (conj! moves [:moveTo last-x last-y]))))

(defn draw-fractal
  "Uses an l-system to draw a fractal on a canvas.

Options:

  Canvas options

   * canvas-name - The name to use for the canvas.
   * clear-rect? - defaults to true

   L-system Options

   * start - The starting string to expand in the l-system
   * depth - The number of recursions
   * rules - The rules to expand the starting string.

   Initial turtle location and direction

   * x - initial x location on canvas
   * y - initial y location on canvas
   * start-direction - start direction in degreese
   * operations - A map of l-system items to commands to run on the turtle"
  [options]
  {:pre [(every? #(contains? options %)
                 [:start :depth :rules :canvas-name :x :y :start-direction :operations])]}
  (let [;; l-system options
        {:keys [start depth rules]} options
        expanded (expand-l-system rules depth start)
        ;; turtle options
        {:keys [x y start-direction operations]} options
        turtle (run-turtle (new-turtle x y start-direction)
                           operations
                           expanded)
        turtle-moves (finalize-moves turtle)
        ;; canvas options
        {:keys [canvas-name clear-rect?]} options]
    (c/draw (:canvas-name options)
            (if (or (nil? clear-rect?) clear-rect?)
              [(c/clear-rect) turtle-moves]
              [turtle-moves]))))

(comment
 (draw-fractal
  {:canvas-name "sierpinski-triangle"
   :x 50.0
   :y 250.0
   :start-direction 0
   :start "a"
   :depth 8
   :rules {\a "b-a-b"
           \b "a+b+a"}
   :operations {\a #(forward % 1)
                \b #(forward % 1)
                \- #(rotate % (Math/toRadians -60.0))
                \+ #(rotate % (Math/toRadians 60.0))}}))
