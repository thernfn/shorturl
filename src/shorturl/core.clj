(ns shorturl.core
  (:require [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :refer [body-params]]
            [ring.util.response :as r]
            [muuntaja.interceptor :as mi]
            [shorturl.db :as db]
            [shorturl.slug :refer [generate-slug]]))

(defn redirect [{{:keys [slug]} :path-params}]
  (if-let [url (db/get-url db/conn slug)]
    (r/redirect url 307)
    (r/not-found "Not Found!")))

(defn create-redirect [{{:keys [url]} :json-params}]
  (let [slug (generate-slug)]
    (db/insert-redirect! db/conn slug url)
    (r/response {:slug slug
                 :url url})))

(def routes
   #{["/" :get [(mi/format-interceptor)
                (fn [_req] {:body {:content "hello"} :status 200})]
      :route-name :hello]
     ["/post/url" :post [(body-params) (mi/format-interceptor) create-redirect]
      :route-name :post-url]
     ["/get/:slug" :get redirect :route-name :redirect]})
