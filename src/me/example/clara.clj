(ns me.example.clara
  {:clj-kondo/config {:ignore true}}
  (:require
   [clara.rules :as cr]
   [clara.rules.accumulators :as a]))


(cr/defrule is-important
  "Find important support request."
  [::support-request [{:keys [level]}] (= :high level)]
  =>
  (println "Hight support requested"))

(cr/defrule notify-client-rep
  "Find the client representative and request support."
  [::support-request [{:keys [level client-name]}] (= ?cname client-name)]
  [::client-representative [{:keys [rep-name client-name]}] (= ?cname client-name) (= ?rname rep-name)]
  =>
  (println "Notify" ?rname "that"
           ?cname "has a new support request"))

;; Accumulator

(cr/defrule update-latest-temperature
  "Update temperature accumulator"
  [?current-temp <- (a/min :temperature :return-facts true) :from [::temperature [{:keys [location]}] (= ?loc location)]]
  =>
  (println "Update temperature:" ?current-temp ?loc))

;; Query

(cr/defquery get-min-temperature
  []
  [?x <- (a/min :temperature :return-facts true) from [::temperature [{:keys [location]}] (= ?loc location)]])

(cr/defquery get-max-temperature
  []
  [?x <- (a/max :temperature :return-facts true) from [::temperature [{:keys [location]}] (= ?loc location)]])

(cr/defquery get-all-temperatures
  []
  [?x <- ::temperature])


(comment
  (-> (cr/mk-session (ns-name *ns*) :fact-type-fn :fact-type)
      (cr/insert {:fact-type ::support-request :level :high :client-name "ACME"}
                 {:fact-type ::client-representative :rep-name "Alice" :client-name "ACME"})
      (cr/fire-rules))

  (-> (cr/mk-session (ns-name *ns*) :fact-type-fn :fact-type)
      (cr/insert {:fact-type ::temperature :location "ID" :timestamp 1 :temperature 37}
                 {:fact-type ::temperature :location "AA" :timestamp 2 :temperature 34}
                 {:fact-type ::temperature :location "BB" :timestamp 3 :temperature 36}
                 {:fact-type ::temperature :location "ID" :timestamp 1 :temperature 30})
      (cr/fire-rules)
      ;; (cr/query get-temperature)
      (as-> $ $
        {:all-temperatures (cr/query $ get-all-temperatures)
         :min-temperature (cr/query $ get-min-temperature)
         :max-temperature (cr/query $ get-max-temperature)})
      )
  ,)
