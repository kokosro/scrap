(ns browser.header
  (:require [clojure.string :as s])
  (:gen-class))

(defn extract-value [h hname]
  (if-not (or (nil? h)
               (nil? hname))
    (let [x (get h hname)]
      (if-not (nil? x)
        x
        ""))
    ""))

(defn extract-name [h hname]
  (if (and (not (nil? h))
           (not (nil? hname)))
  (try
    (let [parts (s/split (extract-value h hname) #";")]
      (first parts))
    (catch Exception e ""))))
(defn extract-info [h hname]
  (if (and (not (nil? h))
           (not (nil? hname)))
  (try
    (let [parts (s/split (extract-value h hname) #";")]
      (s/join "; " (map s/trim (rest parts))))
    (catch Exception e ""))))
