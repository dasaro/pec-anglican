(anglican.emit/defm i-formula [w] (and (= (list (get-in (nth w 12) (keys {:taskCorrect "true"}))) (vals {:taskCorrect "true"})) ))
(anglican.emit/defm conditioning-i-formula [w] true)
