(ns pec-anglican.hprop
  (:require
    [anglican importance smc pcascade pgibbs lmh rmh pimh plmh almh palmh]
    [anglican.stat :as stat])
  (:use
    pec-anglican.generate-world
    [anglican core runtime emit stat])
  (:gen-class))

(defdist dirac
  "Dirac distribution"
  [x]  ; distribution parameters
  []   ; auxiliary bindings
  (sample* [this] x)
  (observe* [this value] (if (= x value) 0.0 (- (/ 1.0 0.0)))))

; Returns true if iformula holds, false otherwise
(with-primitive-procedures [generate-world dirac]
  (defquery hprop [domain-language domain-description i-formula conditioning-i-formula]
      (let [w (generate-world domain-language domain-description)]
        (observe (dirac (conditioning-i-formula w)) true)
        (i-formula w))))

(defn perform-inference
  [domain-language domain-description i-formula conditioning-i-formula number-of-samples]

  ; Defines conditional distribution
  (def hprop-posterior
    ((conditional hprop :importance) domain-language domain-description i-formula conditioning-i-formula))

  (def samples (repeatedly number-of-samples #(sample* hprop-posterior)))
  (time (def freqs (frequencies samples)))
  (map (fn [[value freq]] [value (/ (float freq) number-of-samples)]) freqs))
