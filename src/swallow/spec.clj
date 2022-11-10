(ns swallow.spec
  (:require [clojure.spec.alpha :as s]
            [swallow.exceptions :as ex]
            [expound.alpha :as expound]))

(defn conform
  [spec data]
  (let [result (s/conform spec data)]
    (when (= result ::s/invalid)
      (let [data (s/explain-data spec data)]
        (throw (ex/error :type :validataion
                         :code :spec-validation
                         ::ex/data data))))))

(defn pretty-explain
  ([data] (pretty-explain data nil))
  ([data {:keys [max-problems] :or {max-problems 10}}]
   (when (and (::s/problems data)
              (::s/value data)
              (::s/spec data))
     (binding [s/*explain-out* expound/printer]
       (with-out-str (s/explain-out (update data ::s/problems #(take max-problems %))))))))
