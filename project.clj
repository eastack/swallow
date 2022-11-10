(defproject swsllow "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/spec.alpha "0.3.218"]
                 [integrant "0.8.0"]
                 [com.github.seancorfield/next.jdbc "1.3.834"]
                 [com.mysql/mysql-connector-j "8.0.31"]
                 [com.zaxxer/HikariCP "5.0.1"]
                 [funcool/cuerdas "2022.03.27-397"]
                 [environ "1.2.0"]
                 [expound "0.9.0"]]
  :main ^:skip-aot swsllow.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
