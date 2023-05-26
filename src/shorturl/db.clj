(ns shorturl.db
  (:require [datomic.api :as d]))

(defonce db-uri "datomic:mem://shorturl")
(d/create-database db-uri)
(defonce conn (d/connect db-uri))


(def db-schema
  [{:db/ident :shorturl/url
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The URL to shorten"}
   {:db/ident :shorturl/slug
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Shorted url"}])

(d/transact conn db-schema)

(defn get-url [conn slug]
  (let [db (d/db conn)]
    (-> (d/q '[:find ?url
           :in $ ?slug
           :where
           [?e :shorturl/slug ?slug]
               [?e :shorturl/url ?url]] db slug)
        ffirst)))

(defn insert-redirect! [conn slug url]
  (d/transact conn [{:shorturl/url url
                     :shorturl/slug slug}]))

(comment

  (def sample-url
    [{:shorturl/url "https://github.com"
      :shorturl/slug "abcde"}
     {:shorturl/url "https://google.com"
      :shorturl/slug "xyzay"}])

  (d/create-database db-uri)
  (def conn (d/connect db-uri))

  @(d/transact conn db-schema)
  @(d/transact conn sample-url)
  (d/transact conn [{:shorturl/url "http://lost.video"
                     :shorturl/slug "tyuio"}])
  

  (def db (d/db conn))

  (d/q '[:find ?e ?url ?slug
         :where [?e :shorturl/slug ?slug]
         [?e :shorturl/url ?url]] db)
  ;; => #{[17592186045418 "https://github.com" "abcde"] [17592186045419 "https://google.com" "xyzay"]}

  @(d/transact conn [{:shorturl/url "http://localhost"
                      :shorturl/slug "asdf"}])

  (def new-db (d/db conn))
  (d/q '[:find ?e ?url ?slug
         :where [?e :shorturl/slug ?slug]
         [?e :shorturl/url ?url]] new-db)

  (d/q '[:find ?url
         :where
         [?e :shorturl/slug "asdf"]
         [?e :shorturl/url ?url]] new-db)

  (ffirst #{["bla"]})

  (d/q '[:find ?url
         :in $ ?slug
         :where
         [?e :shorturl/slug ?slug]
         [?e :shorturl/url ?url]] new-db "asdf")

 )
;;(defn query [db q]
;;  (j/query db q))
;;
;;(defn insert! [db q]
;;  (j/db-do-prepared db q))
;;
;;(defn insert-redirect! [db slug url]
;;  (insert! db (-> (h/insert-into :redirects)
;;                  (h/columns :slug :url)
;;                  (h/values
;;                   [[slug url]])
;;                  (sql/format))))
;;
;;(defn get-url [db slug]
;;  (-> (query db
;;             (-> (h/select :*)
;;                 (h/from :redirects)
;;                 (h/where [:= :slug slug])
;;                 (sql/format)))
;;      first
;;      :url))
;;
;;(comment
;;  (j/query mysql-db
;;           ["select * from redirects"])
;;  ;; => ()
;;
;;  (-> (h/select :*)
;;      (h/from :redirects)
;;      (sql/format))
;;  ;; => ["SELECT * FROM redirects"]
;;
;;  (j/query mysql-db
;;           (-> (h/select :*)
;;               (h/from :redirects)
;;               (sql/format)))
;;  ;; => ()
;;
;;  (query mysql-db
;;         (-> (h/select :*)
;;             (h/from :redirects)
;;             (sql/format)))
;;  ;; => ()
;;
;;  (insert! mysql-db
;;           (-> (h/insert-into :redirects)
;;               (h/columns :slug :url)
;;               (h/values
;;                [["abc" "https://github.com/seancorfield/honeysql"]])
;;               (sql/format)))
;;
;;  (query mysql-db
;;         (-> (h/select :*)
;;             (h/from :redirects)
;;             (sql/format)))
;;  ;; => ({:slug "abc", :url "https://github.com/seancorfield/honeysql"})
;;
;;  (insert-redirect! mysql-db "xyz" "https://gitlab.com/srehorn/project-catalog")
;;
;;  (get-url mysql-db "xyz"))
