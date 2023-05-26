(ns shorturl.slug
  (:require [clojure.string :as str]))

(defn generate-slug []
  (-> (random-uuid)
      str
      (str/split #"-")
      first))

(comment
  (first (clojure.string/split (str (random-uuid)) #"-"))
  ;; => "c999517a"
  (generate-slug)
  )
