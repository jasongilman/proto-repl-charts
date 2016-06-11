(ns proto-repl-charts.graph-test
  (:require [proto-repl-charts.graph :as g]
            [loom.graph :as lg]
            [clojure.test :refer :all]))

(deftest convert-graph-data-for-display-test
  (testing "Normal map"
    (is (= {:nodes [{:label :a, :id :a} {:label :b, :id :b} {:label :c, :id :c}]
            :edges [{:from :a, :to :b} {:from :b, :to :c}]
            :options nil}
           (#'g/convert-graph-data-for-display
            {:nodes [:a :b :c]
             :edges [[:a :b] [:b :c]]}
            nil))))
  (testing "Loom graph"
    (let [graph (lg/graph [:a :b] [:b :c])
          converted (#'g/convert-graph-data-for-display graph nil)
          {:keys [nodes edges]} converted]
      (is (= #{{:label :a, :id :a} {:label :b, :id :b} {:label :c, :id :c}}
             (set nodes)))
      (is (= #{{:from :a, :to :b} {:from :b, :to :c}}
             (set edges))))))
