(ns shorturl.system
  (:require [donut.system :as ds]
            [donut.system.repl :as dsr]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [shorturl.env :as configs]
            [shorturl.core :as score]))

(def routes score/routes)

(def service
  "prod configuration"
  {:env :prod
   ::http/routes routes
   ::http/type ::immutant
   ::http/port (:port (configs/get-config :server))})

(def dev-service
  "dev configuration"
  (-> service
      (merge {:env :dev
              ::http/join? false
              ::http/routes #(route/expand-routes (deref #'routes))
              ::http/allowed-origins {:cred true
                                      :allowed-origins
                                      (constantly true)}})
      http/default-interceptors
      http/dev-interceptors))

(def system
  {::ds/defs
   {:http
    {:server #::ds{:start (fn [{:keys [::ds/config]}]
                            (http/start
                             (http/create-server
                              (:service-map config))))
                   :stop (fn [{:keys [::ds/instance]}]
                           (http/stop instance))
                   :config {:service-map service}}}}})


(defmethod ds/named-system :prod
  [_]
  system)

(defmethod ds/named-system :dev
  [_]
  (assoc-in system [::ds/defs
                    :http
                    :server
                    ::ds/config
                    :service-map] dev-service))
(comment
  (dsr/start :dev)
  (dsr/stop)

  ;; Manual
  (def running-system
    (ds/signal system ::ds/start))
  (::ds/instances running-system)
  (ds/signal running-system ::ds/stop)
  
  )
