(ns swallow.db
  (:require [integrant.core :as ig]
            [swallow.migrations :as mg]
            [next.jdbc :as jdbc]
            [next.jdbc.date-time :as jdbc-dt])
  (:import
   com.zaxxer.hikari.HikariConfig
   java.lang.AutoCloseable))

(defn- create-datasource-config
  [{:keys [metrics uri] :as cfg}]
  (let [config (HikariConfig.)]
    (doto config
      (.setJdbcUrl (str "jdbc:" uri))
      (.setPoolName (:name cfg))
      (.setAutoCommit  true)
      (.setReadOnly  false)
      (.setConnectionTimeout  (:connection-timeout cfg))
      (.setValidationTimeout  (:validation-timeout cfg))
      (.setIdleTimeout  (:idle-timeout cfg))
      (.setMaxLifetime  (:max-lifetime cfg))
      (.setMinimumIdle  (:min-size cfg))
      (.setMaximumPoolSize  (:max-size cfg))
      (.setMaximumPoolSize  (:max-size cfg))
      ;(.setConnectionInitSql   "SET statement_timeout = 300000;")
      (.setInitializationFailTimeout  -1))))

(defn create-pool
  [cfg]
  (let [dsc (create-datasource-config cfg)]
    (jdbc-dt/read-as-instant)
    (HikariDataSource. dsc)))

(defn open
  [pool]
  (jdbc/get-connection pool))

(defmethod ig/init-key ::pool
  [_ {:keys [migrations read-only? uri] :as cfg}]
  (if uri
    (let [pool (create-pool cfg)]
      (when-not read-only?
        (some->> (seq migrations) (apply-migrations! pool))))))

(defn- apply-migrations!
  [pool migrations]
  (with-open [conn ^AutoCloseable (open pool)]
    (mg/setup! conn)
    (doseq [[name steps] migrations]
      (mg/migrate! conn {:name (name name) :steps steps}))))

