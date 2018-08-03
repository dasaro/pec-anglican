; --- DOMAIN LANGUAGE ---
(def domain-language
  {:fluents [:tuberculosis]
   :actions [:exposure :reactivation]
   :maxinst 50})

; --- DOMAIN DESCRIPTION ---
; i-proposition:
(def i-proposition-probs [(/ 1 30) (/ 9 30) (/ 2 3)])
(def i-proposition-outcomes [{:tuberculosis "active"} {:tuberculosis "latent"} {:tuberculosis "absent"}])
(def i-proposition
  {:probs i-proposition-probs
   :outcomes i-proposition-outcomes})

; c-proposition:
; c-prop body
(defn exposure-true-and-tuberculosis-absent-precondition [state]
    (and (= (:tuberculosis state) "absent") (= (:exposure state) "true")))
; c-prop probs
(def exposure-true-and-tuberculosis-absent-probs [(/ 4 100) (/ 76 100) (/ 2 10)])
; c-prop outcomes
(def exposure-true-and-tuberculosis-absent-outcomes [{:tuberculosis "active"} {:tuberculosis "latent"} {}])
(def exposure-true-and-tuberculosis-absent
  {:precondition exposure-true-and-tuberculosis-absent-precondition
   :probs exposure-true-and-tuberculosis-absent-probs
   :outcomes exposure-true-and-tuberculosis-absent-outcomes})

; Defines the c-proposition:
; Reactivation=True and Tuberculosis=Latent causes-one-of {({Tuberculosis=Active},1)}
(defn reactivation-true-and-tuberculosis-latent-precondition [state]
    (if (and (= (:tuberculosis state) "latent") (= (:reactivation state) "true"))
    true
    false
  ))
(def reactivation-true-and-tuberculosis-latent-probs [1])
(def reactivation-true-and-tuberculosis-latent-outcomes [{:tuberculosis "active"}])
(def reactivation-true-and-tuberculosis-latent
  {:precondition reactivation-true-and-tuberculosis-latent-precondition
   :probs reactivation-true-and-tuberculosis-latent-probs
   :outcomes reactivation-true-and-tuberculosis-latent-outcomes})

; Defines p-propositions:
; p-propositions have two additional parameters:
; :subject (which defines the action of p-prop) and
; :instant (which defines the instant p-proposition has)
(defn p-prop-1-precondition [state] true)
(def p-prop-1-subject :exposure)
(def p-prop-1-prob (/ 25 100))
(def p-prop-1
  {:subject p-prop-1-subject
   :instant 0
   :precondition p-prop-1-precondition
   ;
   :probs [p-prop-1-prob (- 1 p-prop-1-prob)]
   :outcomes [{p-prop-1-subject "true"}, {p-prop-1-subject "false"}] })


(defn p-prop-2-precondition [state] true)
(def p-prop-2-subject :exposure)
(def p-prop-2-prob (/ 9 10))
(def p-prop-2
  {:subject p-prop-2-subject
   :instant 2
   :precondition p-prop-2-precondition
   ;
   :probs [p-prop-2-prob (- 1 p-prop-2-prob)]
   :outcomes [{p-prop-2-subject "true"}, {p-prop-2-subject "false"}]})

(defn p-prop-schema-precondition [state]
  (if (= (:tuberculosis state) "latent")
    true
    false))
(def p-prop-schema-subject :reactivation)
(def p-prop-schema-prob 0.0008)
(defn p-prop-schema [i]
  {:subject p-prop-schema-subject
   :instant i
   :precondition p-prop-schema-precondition
   ;
   :probs [p-prop-schema-prob (- 1 p-prop-schema-prob)]
   :outcomes [{p-prop-schema-subject "true"}, {p-prop-schema-subject "false"}]})

; not explicitly in domain description (24/5/2018)
(defn p-prop-schema-2-precondition [state] true)
(def p-prop-schema-2-subject :exposure)
(def p-prop-schema-2-prob 0.011)
(defn p-prop-schema-2 [i]
  {:subject p-prop-schema-2-subject
   :instant i
   :precondition p-prop-schema-2-precondition
   ;
   :probs [p-prop-schema-2-prob (- 1 p-prop-schema-2-prob)]
   :outcomes [{p-prop-schema-2-subject "true"}, {p-prop-schema-2-subject "false"}]})

; Domain description:
(def domain-description
  {:iprop i-proposition
   :cprops [exposure-true-and-tuberculosis-absent
   			reactivation-true-and-tuberculosis-latent]
   :pprops (concat
             (vec (map p-prop-schema (range (:maxinst domain-language))))
			 (vec (map p-prop-schema-2 (range 3 (:maxinst domain-language))))
             [(p-prop-schema-2 1)]
             [p-prop-1]
             [p-prop-2])})
