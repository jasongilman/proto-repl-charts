(defproject proto-repl-charts "0.3.0"
  :description "Defines helper functions for displaying graphs and tables in Proto REPL."
  :url "https://github.com/jasongilman/proto-repl-charts"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [aysylu/loom "0.5.4"]]

  :plugins [[lein-codox "0.9.5"]]

  :profiles
  {:dev {:source-paths ["dev" "src" "test"]
         :dependencies [[org.clojure/tools.namespace "0.2.11"]
                        [pjstadig/humane-test-output "0.7.1"]
                        [proto-repl "0.2.0"]]
         :injections [(require 'pjstadig.humane-test-output)
                      (pjstadig.humane-test-output/activate!)]}})
