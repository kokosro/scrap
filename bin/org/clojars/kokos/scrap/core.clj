(ns org.clojars.kokos.scrap.core
  (:require [net.cgrand.enlive-html :as html])
  (:require [clojure.string :as s])  
  (:gen-class))


(defmacro drule
  "creates a rule of extraction"
  [ [element-name selector] 
   extract-code
   & content-rules]
  `{:name (keyword (quote ~element-name))
    :selector ~selector
    :extract (fn [~element-name]
               ~extract-code)
    :content-name :content
    :content [~@content-rules]})

(defmacro defrule [name & rules]
  `(def ~name [~@rules]))

(defmacro an [tag-name selector extract-code & {:keys [with] :or {with []}}]
  `(drule [~tag-name ~selector]
          ~extract-code
          ~@(if (vector? with)
                with
                [with])))


(defn- css-selector [selector]
  (if (string? selector)
    (map keyword (s/split selector #" "))
    selector))

(defn- source [from]
  (if (string? from)
    (html/html-resource (java.io.StringReader. from))
    from))

(defn- rule-selection [page rule]
  (html/select (source page) (css-selector (:selector rule))))

(defn compress-result [result]
  (let [x (reduce concat (list) result)]
    (map (fn [item]
           (let [main-key (first (keys item))]
             (assoc {} main-key (merge (get item main-key) {:content (compress-result (:content (get item main-key)))})))) x)))

(defn make-extractor [rules]
  (fn [page]
    (doall
      (map (fn [rule]
             (doall
               (map (fn [selected]
                      (let [extraction ((:extract rule) selected)]
                        (assoc {} (:name rule)
                           (merge {:value extraction}
                                  (assoc {} (:content-name rule)
                                         ((make-extractor (:content rule)) selected)))))) (rule-selection page rule)))) rules))))

(defn extract [rules from-content]
  (let [extractor (make-extractor rules)
        result (extractor from-content)]
    (compress-result result)))



