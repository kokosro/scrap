(ns org.clojars.kokos.scrap.extractors
  (:require [clojure.string :as s])
  (:gen-class))

(defn content [tag]
  (s/trim (reduce (fn [r x] (if (string? x) (str r x) r)) " " (filter string? (:content tag)))))

(defmacro atribut [tag & path]
  `(-> ~tag :attrs ~@path))

