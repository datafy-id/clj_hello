(ns me.example.clara
  {:clj-kondo/config {:ignore true}}
  (:require
   [clara.rules :as cr]))


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


(comment
  (-> (cr/mk-session (ns-name *ns*) :fact-type-fn :fact-type)
      (cr/insert {:fact-type ::support-request :level :high :client-name "ACME"}
                 {:fact-type ::client-representative :rep-name "Alice" :client-name "ACME"})
      (cr/fire-rules))
  ,)
