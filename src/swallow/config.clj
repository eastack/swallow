(ns swallow.config
  (:require [clojure.core :as c]
            [swallow.exceptions :as ex]
            [cuerdas.core :as str]
            [swallow.spec :as us]
            [environ.core :refer [env]]))

(def defaults
  {:database-uri "mysql://dev-mysql/swallow"
   :database-username "swallow"
   :database-password "swallow"})

(defn read-env
  [prefix]
  (let [prefix (str prefix "-")
        len (count prefix)]
    (reduce-kv
     (fn [acc k v]
       (cond-> acc
         (str/starts-with? (name k) prefix)
         (assoc (keyword (subs (name k) len)) v)))
     {}
     env)))

(defn- read-config
  []
  (try (->> (read-env "swallow")
            (merge defaults)
            (us/conform ::config))
       (catch Throwable e
         (when (ex/ex-info? e)
           (println ";;;;;;;;;;;;;;;")
           (println "配置校验失败：")
           (println (us/pretty-explain (ex-data e)))
           (println ";;;;;;;;;;;;;;;")
           (throw e)))))

(defonce ^:dynamic config (read-config))

(defn get
  "配置获取器。让代码更易于测试。"
  ([key]
   (c/get config key))
  ([key default]
   (c/get config key default)))
