(ns me.hello
  (:gen-class))

(defn -main [& args]
  (println "Hello!" (count args) "arguments passed to main, they are:" (pr-str args)))

(comment
  ;; Run the main from cli like this, e.g. this pass three arguments, "lets" and "go" and "."
  ;; `clojure -M -m me.hello lets go .`
  ,)
