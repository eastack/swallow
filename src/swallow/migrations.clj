(ns swallow.migrations
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [next.jdbc :as jdbc]))

(defn resource
  "辅助生成迁移函数，只需要一个类路径内的简单SQL文件路径"
  [path]
  (fn [pool]
    (let [sql (slurp (io/resource path))]
      (jdbc/execute! pool [sql])
      true)))

(def migrations
  [{:name "001-create-table"
    :fn (resource "migrations/sql/001-create-table.sql")}])

(defn setup!
  "如果没有初始化数据库进行初始化"
  [conn]
  (let [sql (str "create table if not exists migrations ("
                 " module text,"
                 " step text,"
                 " created_at timestamp DEFAULT current_timestamp,"
                 " unique(module, step)"
                 ");")]
    (jdbc/execute! conn [sql])
    nil))

(s/def ::name string?)
(s/def ::step (s/keys :req-un [::name ::fn]))
(s/def ::steps (s/every ::step :kind vector?))
(s/def ::migrations
  (s/keys :req-un [::name ::steps]))

(defn- register!
  "注册一个迁移到正在迁移的数据库"
  [pool modname stepname]
  (let [sql "insert into migrations (module, step) values (?, ?)"]
    (jdbc/execute! pool [sql modname stepname])))

(defn- registered?
  "检查某个迁移是否已经注册了。"
  [pool modname stepname]
  (let [sql "select * from migrations where module=? and step=?"
        rows (jdbc/execute! pool [sql modname stepname])]
    (pos? (count rows))))

(defn- impl-migrate-single
  [pool modname {:keys [name] :as migration}]
  (when-not (registered? pool modname (:name migration))
    (register! pool modname name)
    ((:fn migration) pool)))

(defn- impl-migrate
  [conn migrations _opts]
  (s/assert ::migrations migrations)
  (let [mname (:name migrations)
        steps (:steps migrations)]
    (jdbc/with-transaction [conn conn]
      (run! #(impl-migrate-single conn mname %) steps))))

(defn migrate!
  "应用一个迁移的主入口"
  ([conn migrations]
   (impl-migrate conn migrations nil))
  ([conn migrations options]
   (impl-migrate conn migrations options)))
