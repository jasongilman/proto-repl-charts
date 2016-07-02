(ns proto-repl-charts.canvas-example
  (require [proto-repl-charts.canvas :as c]))

(comment
 (c/draw
  "person"
  [(c/clear-rect)
   (c/stroke-style "red")
   (c/fill-style "red")
   (c/stroke-circle 300 100 40)
   (c/line 300 140 300 240)
   (c/line 300 180 350 160)
   (c/line 300 180 250 160)
   (c/line 300 240 350 280)
   (c/line 300 240 250 280)]))

;; From https://github.com/johnlawrenceaspden/hobby-code/blob/master/fractaltree-stuart-halloway.clj

(defn draw-tree [angle x y length branch-angle depth]
  (when (> depth 0)
    (let [angle (double angle)
          x (double x)
          y (double y)
          length (double length)
          branch-angle (double branch-angle)
          depth (double depth)
          new-x (- x (* length (Math/sin (Math/toRadians angle))))
          new-y (- y (* length (Math/cos (Math/toRadians angle))))
          new-angle1  (+ angle (* branch-angle (+ 0.75 (rand))))
          new-angle2  (- angle (* branch-angle (+ 0.75 (rand))))
          new-length1 (* length (+ 0.75 (rand 0.1)))
          new-length2 (* length (+ 0.75 (rand 0.1)))]

      (c/draw "tree" [(c/line x y new-x new-y)])
      (draw-tree new-angle1 new-x new-y new-length1 branch-angle (- depth 1))
      (draw-tree new-angle2 new-x new-y new-length2 branch-angle (- depth 1)))))

(defn render [w h]
  (c/draw "tree" [(c/clear-rect)])
  (let [init-length ( / (min w h) 5),
        branch-angle (* 10 (/ w h)),
        max-depth 12]
    (draw-tree 0.0 (/ w 2) h init-length branch-angle max-depth)))

;; Faster implementation.
(defn draw-tree-commands
  ([angle x y length branch-angle depth]
   (draw-tree-commands
    (transient [[:beginPath]]) angle x y length branch-angle depth))
  ([cmds angle x y length branch-angle depth]
   (if (> depth 0)
     (let [angle (double angle)
           x (double x)
           y (double y)
           length (double length)
           branch-angle (double branch-angle)
           depth (double depth)
           new-x (- x (* length (Math/sin (Math/toRadians angle))))
           new-y (- y (* length (Math/cos (Math/toRadians angle))))
           new-angle1  (+ angle (* branch-angle (+ 0.75 (rand))))
           new-angle2  (- angle (* branch-angle (+ 0.75 (rand))))
           new-length1 (* length (+ 0.75 (rand 0.1)))
           new-length2 (* length (+ 0.75 (rand 0.1)))
           cmds (-> cmds
                    (conj! [:moveTo (long x) (long y)])
                    (conj! [:lineTo (long new-x) (long new-y)]))
           cmds (draw-tree-commands
                  cmds new-angle1 new-x new-y new-length1 branch-angle (- depth 1))
           cmds (draw-tree-commands
                 cmds new-angle2 new-x new-y new-length2 branch-angle (- depth 1))]
       cmds)
     cmds)))

(defn render-fast [w h]
  (c/draw "Fast render tree" [(c/clear-rect)])
  (let [init-length ( / (min w h) 5),
        branch-angle (* 10 (/ w h)),
        max-depth 12
        cmds (draw-tree-commands
              0.0 (/ w 2) h init-length branch-angle max-depth)
        cmds (persistent!
              (conj! cmds [:stroke]))]
    (println "Sending number of commands:" (count cmds))
    (c/draw "Fast render tree" [cmds])))

(comment
 (time (render 640 400))
 (time (render-fast 640 400))

 ;; A depth of 17 will be this many commands
 (* 2 (Math/pow 2 17)))

;; Other visualization ideas. Spiral using straight lines that curve slightly.
;; change color over time.


;; Based on https://brehaut.net/blog/2011/l_systems

(defn l-system
  [productions depth s]
  (if (zero? depth) s
    (mapcat #(l-system productions
                       (dec depth)
                       (productions % [%]))
            s)))

(comment
 (l-system {\A "AB" \B "A"} 3 "A"))


(defn new-turtle
  [start-x start-y start-direction]
  (transient
   {:direction (Math/toRadians (or start-direction 0.0))
    :curr-x start-x
    :curr-y start-y
    :stack []
    :moves (transient
            [[:beginPath]
             [:moveTo start-x start-y]])}))

(defn run-turtle
  [turtle operations commands]
  (reduce (fn [t cmd]
            ((operations cmd identity) t))
          turtle
          commands))

(defn forward
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

(def ^:static ^:const ^:double TAU
  (* 2.0 Math/PI))

(defn rotate
  [turtle ^double theta]
  (assoc! turtle :direction (mod (+ ^double (:direction turtle) theta) TAU)))

(defn start-branch
  [turtle]
  (let [{:keys [stack curr-x curr-y]} turtle]
    (assoc! turtle :stack (conj stack [curr-x curr-y]))))

(defn end-branch
  [turtle]
  (let [{:keys [stack moves]} turtle
        [last-x last-y] (last stack)]
    (assoc! turtle
            :stack (pop stack)
            :curr-x last-x
            :curr-y last-y
            :moves (conj! moves [:moveTo last-x last-y]))))

(defn finalize-moves
  [turtle]
  (persistent! (conj! (:moves turtle) [:stroke])))

(defn fractal
  [options]
  (let [{:keys [rules x y start operations depth name start-direction]} options
        expanded (l-system rules depth start)
        turtle (run-turtle (new-turtle x y start-direction)
                           operations
                           expanded)]
    (c/draw name
            [(c/clear-rect)
             (finalize-moves turtle)])))

(defn random
  [^double minv ^double maxv]
  (+ (rand (- maxv minv)) minv))

(random 2 5)

(defn value-with-chaos
  ^double [^double chaos ^double v]
  (+ v (random (* -1.0 v chaos) (* v chaos))))

(comment

 (doseq [[distance depth] [[40 1]
                           [30 3]
                           [10 5]
                           [5 7]
                           [1 9]]]
  ;; sierpinski-triangle
  (let [angle (Math/toRadians 60.0)
        neg-angle (* -1.0 angle)]
    (c/draw "sierpinski-triangle"
            [(c/stroke-style "black")
             [[:setTransform 1, 0, 0, 1, 0, 0]
              [:scale 0.5 0.5]]])
    (fractal
     {:name "sierpinski-triangle"
      :start-direction 180.0
      :x 600.0
      :y 500.0
      :start "a"
      :depth depth
      :rules {\a "b-a-b"
              \b "a+b+a"}
      :operations {\a #(forward % distance)
                   \b #(forward % distance)
                   \- #(rotate % neg-angle)
                   \+ #(rotate % angle)}})
   (Thread/sleep 3000)))

 (fractal
  {:name "sierpinski-triangle"
   :x 300.0
   :y 300.0
   :start-direction 180.0
   :start "a"
   :depth 8
   :rules {\a "b-a-b"
           \b "a+b+a"}
   :operations {\a #(forward % 1)
                \b #(forward % 1)
                \- #(rotate % (* -1.0 60.0))
                \+ #(rotate % 60.0)}})




 (let [distance 20
       distance-chaos 0.5
       angle (/ Math/PI 15.0)
       neg-angle (* -1.0 angle)
       angle-chaos 0.2]
   (c/draw "branches"
           [(c/stroke-style "black")
            [[:setTransform 1, 0, 0, 1, 0, 0]
             [:scale 0.5 0.5]]])
   (fractal
    {:name "branches"
     :x 700.0
     :y 700.0
     :start "F"
     :depth 5
     :rules {\F "F[+F]F[--F][+F]"}
     ; :rules {\F "FF[+F][-FF][-F+F]"}
     :operations {\F #(forward % (value-with-chaos distance-chaos distance))
                  \[ start-branch
                  \] end-branch
                  \- #(rotate % (* -1 (value-with-chaos angle-chaos angle)))
                  \+ #(rotate % (value-with-chaos angle-chaos angle))}}))

 (let [distance 7
       distance-chaos 0.4
       angle (Math/toRadians 22.5)
       neg-angle (* -1.0 angle)
       angle-chaos 0.5]
   (fractal
    {:name "tree"
     :x 300.0
     :y 400.0
     :start "F"
     :depth 4
     :rules {\F "FF+[-F+F+F]-[+F-F-F]"}
     :operations {\F #(forward % (value-with-chaos distance-chaos distance))
                  \[ start-branch
                  \] end-branch
                  \- #(rotate % (* -1.0 (value-with-chaos angle-chaos angle)))
                  \+ #(rotate % (value-with-chaos angle-chaos angle))}})))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Drawing rotated polygons

;; 0 at top left


(defn hexagon
  [x y size]
  (let [ds (* 2 size)
        ts (* 3 size)]
   [[x y]
    [(+ x size) y]
    [(+ x ds) (+ y size)]
    [(+ x ds) (+ y ds)]
    [(+ x size) (+ y ts)]
    [x (+ y ts)]
    [(- x size) (+ y ds)]
    [(- x size) (+ y size)]]))

(defn polygon->command-set
  [points]
  (let [[start-x start-y] (first points)]
    (concat
     [[:beginPath]
      [:moveTo start-x start-y]]
     (for [[x y] (rest points)]
       [:lineTo x y])
     [[:closePath]
      [:stroke]])))

(comment
 (let [poly-cmds (vec (concat (polygon->command-set (hexagon -200 -400 200))
                              (polygon->command-set (hexagon -100 -200 100))
                              (polygon->command-set (hexagon -50 -100 50))
                              (polygon->command-set (hexagon -25 -50 25))))
       rotation-amount 5
       rotate [:rotate (Math/toRadians rotation-amount)]
       rotation-cmds (repeat (/ 360 rotation-amount)
                             (conj poly-cmds rotate))]
   (c/draw
    "polygon"
    (concat [[[:setTransform 1, 0, 0, 1, 0, 0]]
             (c/clear-rect)
             ;; Set the center point of the drawing
             [[:translate 800 800]]
             (c/fill-style "black")
             (c/fill-rect -1000 -1000 2000 2000)
             (c/stroke-style "white")]
            rotation-cmds))))
