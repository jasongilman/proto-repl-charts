(ns proto-repl-charts.canvas-test
  (require [clojure.test :refer :all]
           [proto-repl-charts.canvas :as c]))


(def clean-command-sets #'c/clean-command-sets)

(deftest clean-command-sets-test
  (testing "Valid input"
    (are [expected input]
      (= expected
         (clean-command-sets input))

      [[[:foo 1 2 3]]]
      [:foo 1 2 3]

      [[[:foo 1 2 3]]]
      [[:foo 1 2 3]]

      [[[:foo 1 2 3]]]
      [[[:foo 1 2 3]]]

      [[[:foo 1] [:bar 2]]]
      [[:foo 1] [:bar 2]]))
  (testing "Invalid input"
    (are [input]
      (thrown-with-msg?
       Exception #"Command set does not appear to be valid"
       (clean-command-sets input))
      :foo
      []
      1
      "")))

(def validate-command #'c/validate-command)

(deftest validate-command-test
  (testing "valid commands"
    (are [input]
      (= input (validate-command input))
      [:foo]
      [:foo 1]
      [:foo 1 2.0]
      [:foo "string"]
      [:foo true]
      [:foo false]
      [:foo :keyword]
      [:foo nil]))
  (testing "invalid commands"
    (are [input]
      (thrown-with-msg?
       Exception #"Received invalid command"
       (validate-command input))
      []
      [1]
      "foo"
      123)))
