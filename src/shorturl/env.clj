(ns shorturl.env
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(def configs
  (-> (io/resource "config.edn")
      slurp
      (edn/read-string)))

(defn get-config [k]
  (or (k configs) (System/getenv (name k))))

(comment
  configs
  (get-config :server)
  )
