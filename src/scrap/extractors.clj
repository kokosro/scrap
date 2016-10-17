(ns scrap.extractors
  (:require [clojure.string :as s])
  (:gen-class))

(defn content [tag]
  (s/trim (reduce (fn [r x] (if (string? x) 
                              (str r x) 
                              r)) 
                  " " 
                  (filter string? (:content tag)))))

(defn attributs [tag match-regex]
  (let [attrs (map #(re-find (re-matcher match-regex (name %))) (keys (:attrs tag)))
        ]
    (reduce (fn [r k-v]
              (if-not (nil? (last k-v))
                (assoc r (keyword (last k-v))
                  (get (:attrs tag) (keyword (last k-v))))
                r)) 
            {}
            attrs)))

(defn data-attrs [tag]
  (attributs tag #"data\-(.*)"))

(defmacro atribut [tag & path]
  `(-> ~tag :attrs ~@path))

(defmacro attribut [& body]
  `(atribut ~@body))

