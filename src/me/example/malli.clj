(ns me.example.malli
  (:require
   [malli.core :as m]
   [malli.experimental.lite :as l]
   [malli.transform :as mt]))

(def UserId
  (m/schema :string))

(def Address
  (m/schema
    [:map
     [:street :string]
     [:country [:enum "ID" "MY"]]]))

(def User
  (m/schema
    [:map
     [:id #'UserId]
     [:address #'Address]
     [:friends [:set {:gen/max 2} [:ref #'User]]]]))

;; Lite Syntax
(def Address2
  (l/schema
    {:id :string
     :tags [:set :keyword]
     :address
     (l/schema
       {:street :string
        :city :string
        :zip :int
        :lonlat [:tuple :double :double]})}))


(comment
  ,

  (m/schema? :int) ;; => false
  (m/schema? (m/schema :int)) ;; => true
  (m/schema? User)
  (m/schema? Address2) ;; => true

  ;; Schema Definition with vector syntax
  ;;   type
  ;;   [type & children]
  ;;   [type props & children]

  :int
  [:tuple :keyword]
  [:map {:closed true} [:name :string] [:age :int]]

  (m/validate :int 1) ;; => true
  (m/validate :int "1") ;; => false
  (m/validate [:int {:min 10}] 10) ;; => true
  (m/validate [:int {:min 10}] 9) ;; => false
  (m/validate User {:id "ridho"
                    :address {:street "Anyer Street"
                              :country "ID"}
                    :friends #{{:id "root"
                                :address {:street ""
                                          :country "MY"}
                                :friends #{}}}}) ;; => true

  (m/validate Address2 {:id "u1"
                        :tags #{:home :daily}
                        :address {:street "Anyer Street"
                                  :city "Cilegon"
                                  :zip 42431
                                  :lonlat [0.0 0.0]}}) ;; => true



  ;; TRANSFORMER
  ;; `decode` will try to coerce INPUT to expected type described by the schema,
  ;; if coercion failed, return input value as is.

  ;; IN --decode--> VAL --encode--> OUT

  (-> (m/decode :int "10" mt/string-transformer)
      (as-> $ (m/encode :int $ mt/string-transformer)))

  (m/decode :double 1 mt/json-transformer) ;; => 1.0
  (m/decode :double "1" mt/json-transformer) ;; => "1" ! RETURN original if can not transform
  (m/decode :double 1.02 mt/json-transformer) ;; => 1.02

  (m/decode :int "1" mt/string-transformer) ;; => 1
  (m/decode :int 1.1 mt/string-transformer) ;; => 1.1
  (m/decode :int "1.2" mt/string-transformer) ;; => "1.2"

  (m/decode :double "1" mt/string-transformer) ;; => 1.0
  (m/decode :double 1 mt/string-transformer) ;; => 1

  ,)


