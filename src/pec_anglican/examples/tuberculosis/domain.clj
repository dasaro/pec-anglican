; --- DOMAIN LANGUAGE ---
; (def domain-language
;  {:fluents [:tuberculosis]
;   :actions [:exposure :reactivation]
;   :maxinst 50})
(def domain-language {})
(def domain-description {})
(alter-var-root #'domain-description #(assoc-in % [:cprops] []))
(alter-var-root #'domain-description #(assoc-in % [:pprops] []))

(alter-var-root #'domain-language #(assoc % :fluents (conj (:fluents domain-language) :tuberculosis)))
(alter-var-root #'domain-language #(assoc % :actions (conj (:actions domain-language) :exposure)))
(alter-var-root #'domain-language #(assoc % :actions (conj (:actions domain-language) :reactivation)))
(alter-var-root #'domain-language #(assoc % :maxinst 50))

; Defines p-propositions:
; p-propositions have two additional parameters:
; :subject (which defines the action of p-prop) and
; :instant (which defines the instant p-proposition has)

; (defn p-prop-schema-precondition [state]
;   (if (= (:tuberculosis state) "latent")
;     true
;     false))
; (def p-prop-schema-subject :reactivation)
; (def p-prop-schema-prob 0.0008)
; (defn p-prop-schema [i]
;   {:subject p-prop-schema-subject
;    :instant i
;    :precondition p-prop-schema-precondition
;    ;
;    :probs [p-prop-schema-prob (- 1 p-prop-schema-prob)]
;    :outcomes [{p-prop-schema-subject "true"}, {p-prop-schema-subject "false"}]})
;
; ; not explicitly in domain description (24/5/2018)
; (defn p-prop-schema-2-precondition [state] true)
; (def p-prop-schema-2-subject :exposure)
; (def p-prop-schema-2-prob 0.011)
; (defn p-prop-schema-2 [i]
;   {:subject p-prop-schema-2-subject
;    :instant i
;    :precondition p-prop-schema-2-precondition
;    ;
;    :probs [p-prop-schema-2-prob (- 1 p-prop-schema-2-prob)]
;    :outcomes [{p-prop-schema-2-subject "true"}, {p-prop-schema-2-subject "false"}]})

; --- DOMAIN DESCRIPTION ---
; i-proposition:
; (def i-proposition-probs [(/ 1 30) (/ 9 30) (/ 2 3)])
; (def i-proposition-outcomes [{:tuberculosis "active"} {:tuberculosis "latent"} {:tuberculosis "absent"}])
; (def i-proposition
;   {:probs i-proposition-probs
;    :outcomes i-proposition-outcomes})
(alter-var-root #'domain-description #(assoc-in % [:iprop :probs] (vec (conj (get-in % [:iprop :probs]) (/ 1 30)))))
(alter-var-root #'domain-description #(assoc-in % [:iprop :outcomes] (vec (conj (get-in % [:iprop :outcomes]) {:tuberculosis "active"}))))
(alter-var-root #'domain-description #(assoc-in % [:iprop :probs] (vec (conj (get-in % [:iprop :probs]) (/ 9 30)))))
(alter-var-root #'domain-description #(assoc-in % [:iprop :outcomes] (vec (conj (get-in % [:iprop :outcomes]) {:tuberculosis "latent"}))))
(alter-var-root #'domain-description #(assoc-in % [:iprop :probs] (vec (conj (get-in % [:iprop :probs]) (/ 2 3)))))
(alter-var-root #'domain-description #(assoc-in % [:iprop :outcomes] (vec (conj (get-in % [:iprop :outcomes]) {:tuberculosis "absent"}))))

; C-PROPOSITIONS
; (defn reactivation-true-and-tuberculosis-latent-precondition [state]
;     (if (and (= (:tuberculosis state) "latent") (= (:reactivation state) "true"))
;     true
;     false
;   ))
; (def reactivation-true-and-tuberculosis-latent-probs [1])
; (def reactivation-true-and-tuberculosis-latent-outcomes [{:tuberculosis "active"}])
; (def reactivation-true-and-tuberculosis-latent
;   {:precondition reactivation-true-and-tuberculosis-latent-precondition
;    :probs reactivation-true-and-tuberculosis-latent-probs
;    :outcomes reactivation-true-and-tuberculosis-latent-outcomes})
(alter-var-root #'domain-description #(assoc-in % [:cprops 0 :precondition] (fn [state] (and (= (list (get-in state (keys {:tuberculosis "latent"}))) (vals {:tuberculosis "latent"}))(= (list (get-in state (keys {:reactivation "true"}))) (vals {:reactivation "true"}))))))
(alter-var-root #'domain-description #(assoc-in % [:cprops 0 :outcomes] (vec (conj (get-in % [:cprops 0 :outcomes]) {:tuberculosis "active"}))))
(alter-var-root #'domain-description #(assoc-in % [:cprops 0 :probs] (vec (conj (get-in % [:cprops 0 :probs]) 1))))

; (defn exposure-true-and-tuberculosis-absent-precondition [state]
;     (and (= (:tuberculosis state) "absent") (= (:exposure state) "true")))
; ; c-prop probs
; (def exposure-true-and-tuberculosis-absent-probs [(/ 4 100) (/ 76 100) (/ 2 10)])
; ; c-prop outcomes
; (def exposure-true-and-tuberculosis-absent-outcomes [{:tuberculosis "active"} {:tuberculosis "latent"} {}])
; (def exposure-true-and-tuberculosis-absent
;   {:precondition exposure-true-and-tuberculosis-absent-precondition
;    :probs exposure-true-and-tuberculosis-absent-probs
;    :outcomes exposure-true-and-tuberculosis-absent-outcomes})

(alter-var-root #'domain-description #(assoc-in % [:cprops 1 :precondition] (fn [state] (and (= (list (get-in state (keys {:tuberculosis "absent"}))) (vals {:tuberculosis "absent"})) (= (list (get-in state (keys {:exposure "true"}))) (vals {:exposure "true"}))))))

(alter-var-root #'domain-description #(assoc-in % [:cprops 1 :outcomes] (vec (conj (get-in % [:cprops 1 :outcomes]) {:tuberculosis "active"}))))
(alter-var-root #'domain-description #(assoc-in % [:cprops 1 :probs] (vec (conj (get-in % [:cprops 1 :probs]) (/ 4 100)))))

(alter-var-root #'domain-description #(assoc-in % [:cprops 1 :outcomes] (vec (conj (get-in % [:cprops 1 :outcomes]) {:tuberculosis "latent"}))))
(alter-var-root #'domain-description #(assoc-in % [:cprops 1 :probs] (vec (conj (get-in % [:cprops 1 :probs]) (/ 76 100)))))

(alter-var-root #'domain-description #(assoc-in % [:cprops 1 :outcomes] (vec (conj (get-in % [:cprops 1 :outcomes]) {}))))
(alter-var-root #'domain-description #(assoc-in % [:cprops 1 :probs] (vec (conj (get-in % [:cprops 1 :probs]) (/ 2 10)))))

; (defn p-prop-2-precondition [state] true)
; (def p-prop-2-subject :exposure)
; (def p-prop-2-prob (/ 9 10))
; (def p-prop-2
;   {:subject p-prop-2-subject
;    :instant 2
;    :precondition p-prop-2-precondition
;    ;
;    :probs [p-prop-2-prob (- 1 p-prop-2-prob)]
;    :outcomes [{p-prop-2-subject "true"}, {p-prop-2-subject "false"}]})

(alter-var-root #'domain-description #(assoc-in % [:pprops 0 :precondition] (fn [state] true)))

(alter-var-root #'domain-description #(assoc-in % [:pprops 0 :subject] :exposure))

(alter-var-root #'domain-description #(assoc-in % [:pprops 0 :instant] 2))

(alter-var-root #'domain-description #(assoc-in % [:pprops 0 :outcomes] (vec (conj (get-in % [:pprops 0 :outcomes]) {:exposure "true"}))))
(alter-var-root #'domain-description #(assoc-in % [:pprops 0 :probs] (vec (conj (get-in % [:pprops 0 :probs]) (/ 9 10)))))

(alter-var-root #'domain-description #(assoc-in % [:pprops 0 :outcomes] (vec (conj (get-in % [:pprops 0 :outcomes]) {:exposure "false"}))))
(alter-var-root #'domain-description #(assoc-in % [:pprops 0 :probs] (vec (conj (get-in % [:pprops 0 :probs]) (- 1 (/ 9 10))))))

; (defn p-prop-1-precondition [state] true)
; (def p-prop-1-subject :exposure)
; (def p-prop-1-prob (/ 25 100))
; (def p-prop-1
;   {:subject p-prop-1-subject
;    :instant 0
;    :precondition p-prop-1-precondition
;    ;
;    :probs [p-prop-1-prob (- 1 p-prop-1-prob)]
;    :outcomes [{p-prop-1-subject "true"}, {p-prop-1-subject "false"}] })

(alter-var-root #'domain-description #(assoc-in % [:pprops 1 :precondition] (fn [state] true)))

(alter-var-root #'domain-description #(assoc-in % [:pprops 1 :subject] :exposure))

(alter-var-root #'domain-description #(assoc-in % [:pprops 1 :instant] 0))

(alter-var-root #'domain-description #(assoc-in % [:pprops 1 :outcomes] (vec (conj (get-in % [:pprops 1 :outcomes]) {:exposure "true"}))))
(alter-var-root #'domain-description #(assoc-in % [:pprops 1 :probs] (vec (conj (get-in % [:pprops 1 :probs]) (/ 25 100)))))

(alter-var-root #'domain-description #(assoc-in % [:pprops 1 :outcomes] (vec (conj (get-in % [:pprops 1 :outcomes]) {:exposure "false"}))))
(alter-var-root #'domain-description #(assoc-in % [:pprops 1 :probs] (vec (conj (get-in % [:pprops 1 :probs]) (- 1 (/ 25 100))))))
