(ns pec-anglican.hprop
  (:require
    [anglican importance smc pcascade pgibbs lmh rmh pimh plmh almh palmh]
    [anglican.stat :as stat])
  (:use
    pec-anglican.generate-world
    [anglican core runtime emit stat
    [inference :only [infer equalize]]
    [state :only [get-predicts get-log-weight set-log-weight]]]))

; Returns true if iformula holds, false otherwise
(with-primitive-procedures [generate-world]
  (defquery hprop
    [domain-language domain-description i-formula]
    (let [w (generate-world domain-language domain-description)]
      (i-formula w))))

(defn perform-inference
  [domain-language domain-description i-formula number-of-samples]

  ; Defines conditional distribution
  (def hprop-posterior
    ((conditional hprop :pimh :number-of-particles 100) domain-language domain-description i-formula))

  (def samples (repeatedly number-of-samples #(sample* hprop-posterior)))
  (time (def freqs (frequencies samples)))
  (map (fn [[value freq]] [value (/ (float freq) number-of-samples)]) freqs))
