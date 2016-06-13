(prc/canvas
 "Person"
 [[:clearRect [0 0 2000 2000]]
  [:fillRect [50,50,120,120]]
  [:beginPath []]
  [:moveTo [300 30]]
  [:lineTo [300 240]]
  [:stroke []]])

(prc/canvas
 "Circle"
 [[:clearRect [0 0 2000 2000]]
  [:beginPath []]
  [:arc [150 105 70 0 (* 2 Math/PI) false]]
  [:fill []]
  [:stroke []]])

(defn draw-line
  [x1 y1 x2 y2]
  (proto-repl.extension-comm/send-command
   "proto-repl-charts"
   {:type "canvas"
    :name "Test"
    :data [[:beginPath []]
           [:moveTo [x1 y1]]
           [:lineTo [x2 y2]]
           [:stroke []]]}))

(defn width
  []
  (proto-repl.extension-comm/send-command
   "proto-repl-charts"
   {:type "canvas"
    :name "Test"
    :data [[:width]]}
   {:wait-for-response? true}))

(defn height
  []
  (proto-repl.extension-comm/send-command
   "proto-repl-charts"
   {:type "canvas"
    :name "Test"
    :data [[:height]]}
   {:wait-for-response? true}))

(proto-repl.extension-comm/send-command
 "proto-repl-charts"
 {:type "canvas"
  :name "Test"
  ;; TODO figure out how to get things from the canvas itself and not the context
  ;; There are definitely times when we'll want either. Or maybe just width and height
  ;; as specific commands.
  :data [[:get [:lineWidth]]]}
 {:wait-for-response? true})


(defn clear
  []
  (proto-repl.extension-comm/send-command
   "proto-repl-charts"
   {:type "canvas"
    :name "Test"
    :data [[:clearRect [0 0 2000 2000]]]}))

(do
 (clear)
 (doseq [x (range 1 10 0.5)
         y (range 1 10 0.5)]
  (draw-line (* x 50) (* y 50) (* x 100) (* 100 y))))
  ; (Thread/sleep 50)))

(do
 (proto-repl.extension-comm/request
  "proto-repl-charts"
  {:type "canvas"
   :name "Test"
   :data [[:clearRect [0 0 2000 2000]]
          [:beginPath []]
          [:arc [150 105 70 0 (* 2 Math/PI) false]]
          [:fill []]
          [:stroke []]]})
 nil)
