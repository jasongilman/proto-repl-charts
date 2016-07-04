(ns proto-repl-charts.examples.canvas.rotating-polygon
  (require [proto-repl-charts.canvas :as c]))

(def ^:const ^:private ^:double TAU
  "2 PI - aka a complete circle in radians"
  (* 2.0 Math/PI))

(defn point-from
  "Returns a new point at the given distance and angle from another point"
  [^double x ^double y ^double angle ^double distance]
  [(+ x (* distance (Math/sin angle)))
   (+ y (* distance (Math/cos angle)))])

(defn n-sided-polygon
  "Creates a polygon with N sides along a circle with the given radius"
  [center-x center-y ^long num-sides radius]
  (let [angle (/ TAU (double num-sides))]
    (for [n (range num-sides)]
      (point-from center-x center-y (* angle n) radius))))

(defn draw-rotated-polygon
  "Creates a drawing by repeatedly drawing a polygon many times in a circle with
   a varied radius. Options are the following:

   * canvas-name - Name of the canvas to use.
   * size - The size of the drawing. Used for positioning the origin.
   * num-sides - The number of sides of the polygon.
   * num-polygons - The number of polygons to draw.
   * polygon-size-fn - Function taking polygon number and returning the size of
     polygon to create.
   * radius-fn - Function taking polygon number and returning the radius from the
     origin of the polygon
   * rotation-amount - Amount to rotate between each polygon.
   * background-style - Style to use for coloring the background.
   * polygon-style - Style to use for coloring the polygon."
  [options]
  (let [{:keys [canvas-name num-sides polygon-size-fn radius-fn
                num-polygons background-style polygon-style
                rotation-amount size]} options]
    ;; Open the canvas if not yet open and reset any transformations
    (c/draw canvas-name
            [(c/transform-reset)
             (c/clear-rect)
             (c/fill-style background-style)
             (c/fill-rect 0 0 2000 2000)
             (c/stroke-style polygon-style)])

    (let [;; Define the commands to draw the polygons
          polygon-cmds (for [n (range num-polygons)]
                         (concat
                          (c/stroke-polygon
                           (n-sided-polygon 0
                                            (radius-fn n)
                                            num-sides
                                            (polygon-size-fn n)))
                          (c/rotate rotation-amount)))]
      (c/draw
       canvas-name
       (concat [(c/translate (/ size 2.0) (/ size 2.0))]
               polygon-cmds)))))
