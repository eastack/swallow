(ns swallow.exceptions
  (:require [clojure.spec.alpha :as s]))

(s/def :error-params
  (s/keys :req-un [::type]
          :opt-un [::code
                   ::hint
                   ::cause]))

(defn error
  [& {:keys [hint cause ::data type] :as params}]
  (s/assert ::error-params params)
  (let [payload (-> params
                    (dissoc :cause ::data)
                    (merge data))
        hint (or hint (pr-str type))]
    (ex-info hint payload cause)))
