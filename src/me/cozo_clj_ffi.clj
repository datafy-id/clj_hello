(ns me.cozo-clj-ffi
  (:require
   [coffi.ffi :as ffi :refer [defcfn]]
   [coffi.mem :as mem]
   ,))

(defcfn strlen
  "strlen" [::mem/c-string] ::mem/long
  native-str-len [s]
  (str "The string length is: " (native-str-len s)))

(comment
  (strlen "hello world!")
  ,)

(ffi/load-system-library "cozo_c-0.7.6-aarch64-apple-darwin")

(defcfn cozo-free-str
  "cozo_free_str" [::mem/pointer] ::mem/void)

(defcfn cozo-open-db
  "cozo_open_db" [::mem/c-string ::mem/c-string ::mem/c-string ::mem/pointer] ::mem/pointer
  native-cozo-open-db [engine path options]
  (with-open [arena (mem/confined-arena)]
    (let [int-ptr (mem/alloc-instance ::mem/int arena)
          error-result-ptr (native-cozo-open-db engine path options int-ptr)
          ]
      (if error-result-ptr
        (let [msg (mem/deserialize error-result-ptr ::mem/c-string)]
          (cozo-free-str error-result-ptr)
          (throw (ex-info (str "ERROR: " msg) {:engine engine :path path :options options})))
        (mem/read-int int-ptr)))))

(defcfn cozo-run-query
  "cozo_run_query" [::mem/int ::mem/c-string ::mem/c-string ::mem/int] ::mem/c-string)

(defcfn cozo-close-db
  "cozo_close_db" [::mem/int] ::mem/int
  native-cozo-close-db [db-id]
  (let [x (native-cozo-close-db db-id)]
    (not (zero? x))))

(comment
  (def dbid (cozo-open-db "rocksdb" "cozo_data" "{}"))
  (cozo-run-query dbid "?[] <- [[1]]" "{}" 1)
  (cozo-close-db dbid)
  ,)
