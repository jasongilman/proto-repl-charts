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
