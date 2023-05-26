(ns shorturl.db
  (:require [clojure.java.jdbc :as j]
            [honeysql.core :as sql]
            [honeysql.helpers :as h]
            [shorturl.env :as configs]))

(def mysql-db
  (let [db-config (configs/get-config :db)]
    {:dbtype (:dbtype db-config)
     :host (:host db-config)
     :dbname (:dbname db-config)
     :user (:user db-config)
     :password (:password db-config)}))

(defn query [db q]
  (j/query db q))

(defn insert! [db q]
  (j/db-do-prepared db q))

(defn insert-redirect! [db slug url]
  (insert! db (-> (h/insert-into :redirects)
                  (h/columns :slug :url)
                  (h/values
                   [[slug url]])
                  (sql/format))))

(defn get-url [db slug]
  (-> (query db
             (-> (h/select :*)
                 (h/from :redirects)
                 (h/where [:= :slug slug])
                 (sql/format)))
      first
      :url))

(comment
  (j/query mysql-db
           ["select * from redirects"])
  ;; => ()

  (-> (h/select :*)
      (h/from :redirects)
      (sql/format))
  ;; => ["SELECT * FROM redirects"]

  (j/query mysql-db
           (-> (h/select :*)
               (h/from :redirects)
               (sql/format)))
  ;; => ()

  (query mysql-db
         (-> (h/select :*)
             (h/from :redirects)
             (sql/format)))
  ;; => ()

  (insert! mysql-db
           (-> (h/insert-into :redirects)
               (h/columns :slug :url)
               (h/values
                [["abc" "https://github.com/seancorfield/honeysql"]])
               (sql/format)))

  (query mysql-db
         (-> (h/select :*)
             (h/from :redirects)
             (sql/format)))
  ;; => ({:slug "abc", :url "https://github.com/seancorfield/honeysql"})

  (insert-redirect! mysql-db "xyz" "https://gitlab.com/srehorn/project-catalog")

  (get-url mysql-db "xyz")
  )
