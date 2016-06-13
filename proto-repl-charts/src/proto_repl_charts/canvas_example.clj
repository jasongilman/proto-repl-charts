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
