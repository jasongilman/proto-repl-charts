(defproject proto-repl-charts "0.3.2"
  :description "Defines helper functions for displaying graphs and tables in Proto REPL."
  :url "https://github.com/jasongilman/proto-repl-charts"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [aysylu/loom "0.5.4"]]

  ;; Documentation can be generated with `lein codox`
  :plugins [[lein-codox "0.9.5"]]
  :codox {:output-path "doc"
          :source-paths ["src"]}

  :profiles
  {:dev {:source-paths ["dev" "src" "test"]
         :dependencies [[org.clojure/tools.namespace "0.2.11"]
                        [pjstadig/humane-test-output "0.7.1"]
                        [proto-repl "0.3.1"]]
         :injections [(require 'pjstadig.humane-test-output)
                      (pjstadig.humane-test-output/activate!)]}})
