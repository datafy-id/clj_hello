(ns me.example.logic
  {:clj-kondo/config '{:lint-as {clojure.core.logic/defne clojure.core/defn}
                       :ignore true}}
  (:require [clojure.core.logic :as l]))


(l/defne head-is-1 [_q]
  ([(1 . _)]))

(l/defne tail-is-789 [_q]
  ([(_ . [7 8 9])]))

(comment
  (l/run* [q]
    (tail-is-789 q)
    (head-is-1 q)) ;; => ((1 7 8 9))

  (l/run* [q]
    (l/== q (range 4))
    (l/matche [q]
              ([ (a . [1 2 _]) ]))) ;;=> ((0 1 2 3))

  (l/run* [q]
    (l/fresh [a b]
      (l/== b [:one 1 2 :four])
      (l/matche [b]
                ([ (a . [1 2 _]) ]))
      (l/== a q))) ;; => (:one)

  (l/run* [q]
    (l/membero q [-3 0 1 2 3 4 11])
    (l/pred q #(> % 5))) ;; => (11)

  ,)
