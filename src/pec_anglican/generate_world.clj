(ns pec-anglican.generate-world
  (:require
    [anglican importance smc pcascade pgibbs lmh rmh pimh plmh almh palmh]
    [anglican.stat :as stat])
  (:use
    [anglican core runtime emit stat
    [inference :only [infer equalize]]
    [state :only [get-predicts get-log-weight set-log-weight]]]))

; Function that samples an outcome from an (i-, c-, p-) proposition
(defn choose-outcome [prop]
  ; Check that proposition exists
  (if prop
    (let [probs (:probs prop)
          outcomes (:outcomes prop)
          choice (sample* (discrete probs))]
      (nth outcomes choice))
    ; If proposition does not exist,
    ; then no outcome is generated:
    {}))

; Given fluent state S and partial state S', returns S⊕S'
(defn oplus [state partial_state]
  (into state partial_state))

(defn union [fluentState actions]
  (into fluentState actions))

; Given a (partial) state S, returns S|F:
(defn restriction-fluent [domain-language state]
  (select-keys state (:fluents domain-language)))

; Given a (partial) state S, returns S|A:
(defn restriction-action [domain-language state]
  (select-keys state (:actions domain-language)))

; The two following defs check whether the
; precondition of a (c-, p-) proposition
; is satisfied in a given state.
; -
; They RETURN proposition if so, false otherwise.
(defn c-prop-satisfied? [state]
  (fn [c]
    (if ((:precondition c) state)
      c
      false)))

(defn p-prop-satisfied? [fluentState subject instant]
  (fn [p]
    (if (and (= (:instant p) instant)
              (= (:subject p) subject)
              ((:precondition p) fluentState))
      p
      false)))

; Definition of "occ" (WARNING: it works on states rather than instants)
; Returns the unique c-proposition activated in state S, if there is one
(defn occ [domain-description state]
  (some (c-prop-satisfied? state) (:cprops domain-description)))

; Transition function:
  (defn transition-function [domain-language domain-description state]
    (let [c (occ domain-description state)
          fluentState (restriction-fluent domain-language state)]
      (if c
        (oplus fluentState (choose-outcome c))
        fluentState)))

; Defines "default" action state
; with all actions set to "false"
(defn default-action-state [domain-language]
	(zipmap (:actions domain-language) (repeat "false")))

; Restricts state to actions only
(defn get-action-state [domain-language domain-description fluentState instant]
  (oplus (default-action-state domain-language)
  (apply merge
         (map choose-outcome
              (map #(some (p-prop-satisfied? fluentState % instant) (:pprops domain-description))
                   (:actions domain-language))))))

; --- FUNCTION THAT GENERATES A WELL-BEHAVED WORLD ---
(defn generate-world [domain-language domain-description]
  ; Unpacks domain description
  ; and samples initial state:
  (let [i-prop (:iprop domain-description)
        c-props (:cprops domain-description)
        p-props (:pprops domain-description)
        initialFluentState (choose-outcome i-prop)
        initialActions (get-action-state domain-language domain-description initialFluentState 0)
        initialState (union initialFluentState initialActions)]
  (loop [inst 0
         w [initialState]]
    (if (= inst (:maxinst domain-language))
      w
      (recur
        (+ inst 1)
        (let [newFluentState (transition-function domain-language domain-description (w inst))
              newActionState (get-action-state domain-language domain-description newFluentState (+ inst 1))]
          (conj w (union newFluentState newActionState))))))))
