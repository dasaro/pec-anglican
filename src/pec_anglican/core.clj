(ns pec-anglican.core
  (:require
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [pec-anglican.hprop :as hprop])
  (:gen-class))

(def cli-options
  [["-h" "--help" "Displays help"]

   ["-n" "--number-of-samples N" "Sets the number of samples (default: 1000)"
    :default 1000
    :parse-fn #(Integer/parseInt %)
    :validate [#(> % 0)
               "number of samples must be strictly positive."]]
   ["-q" "--query Q" "Sets the query (mandatory)"
    ;:validate [#(!= % "") "query must be non-empty"]
    ]])

(defn usage
  [options-summary]
  (->> [options-summary] (string/join \newline)))

(defn error-msg
  [errors]
  (str "The following errors occurred while parsing your command:\n"
       errors))

(defn exit
  [status msg]
  (println msg)
  (System/exit status))

(defn validate-args
  "Validate command line arguments. Either return a map indicating the program
  should exit (with an error message, and optional ok status), or a map
  indicating the action the program should take and the options provided."
  [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) ; help => exit OK with usage summary
        {:exit-message (usage summary) :ok? true}

      errors ; errors => exit with description of errors
        {:exit-message (error-msg errors)}

      ;; custom validation on arguments
      (and (= 1 (count arguments)) (:query options))
        {:domain-file (first arguments) :options options}

      :else ; failed custom validation => exit with usage summary
        {:exit-message (error-msg "Please enter exactly one domain description and a query (using option -q).")})))

(defn load-domain
  "Loads domain description from clojure module. Returns a pointer to
  domain description. WARNING: Any domain description must be defined
  as (def domain-description) in the corresponding file."
  [domain-file]
  (require (symbol domain-file) :reload)
  ([(var-get (or (ns-resolve (symbol domain-file) (symbol "domain-language"))
               (throw (Exception. (format "%s/domain-language was not found"
                                          domain-file)))))
   (var-get (or (ns-resolve (symbol domain-file) (symbol "domain-description"))
               (throw (Exception. (format "%s/domain-description was not found"
                                          domain-file)))))]))

(defn -main
  [& args]

  (let [{:keys [domain-file options exit-message ok?]} (validate-args args)
        query-file (:query options)]

        (println "Loading domain and query...")
        (time (load-file domain-file))
        (load-file query-file)
        (print "\n")

        ; Exits displaying error message if needed
        (if exit-message
          (exit (if ok? 0 1) exit-message))

        (let [domain-language @(resolve 'domain-language)
              domain-description @(resolve 'domain-description)
              i-formula @(resolve 'i-formula)
              conditioning-i-formula @(resolve 'conditioning-i-formula)
              number-of-samples (:number-of-samples options)]

              ; Outputs summary
              (println
                (format (str "Running program on following parameters:\n"
                  ";; Domain: %s\n"
                  ;";; Inference algorithm: %s %s\n"
                  ";; Query: %s\n"
                  ";; Number of samples: %s")
                    domain-file query-file number-of-samples))

              (println (hprop/perform-inference domain-language domain-description i-formula conditioning-i-formula number-of-samples)))))
