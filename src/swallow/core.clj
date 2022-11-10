(ns swallow.core
  (:require [swallow.config :as cf]
            [integrant.core :as ig])
  (:gen-class))

(def config
  {:swallow.db/pool
   {:uri (cf/get :database-uri)
    :username (cf/get :database-username)
    :password (cf/get :database-password)
    :migrations (ig/ref :swallow.migrations/all)}

   :swallow.migrations/migrations
   {}

   :swallow.migrations/all
   {:main (ig/ref :swallow.migrations/migrations)}})

(def system nil)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (ig/load-namespaces config)
  (alter-var-root #'system (fn [sys]
                             (when sys (ig/halt! sys))
                             (ig/init config)))
  (println "Hello, World!"))
