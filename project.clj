(defproject pec-anglican "0.0.1-SNAPSHOT"
  :description "An Anglican implementation of the PEC+ framework"
  :url "http://github.com/dasaro/PEC"
  :license {:name "GNU General Public License Version 3; Other commercial licenses available."
            :url "http://www.gnu.org/licenses/gpl.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.7"]
                 [org.clojure/data.csv "0.1.3"]
                 [clj-auto-diff "0.1.3"]
                 [anglican "1.0.0"]]
  :plugins [[dtolpin/lein-gorilla "0.4.1-SNAPSHOT"]]
  :main ^:skip-aot pec-anglican.core ;anglican.core
  :profiles {:uberjar {:aot :all}})
