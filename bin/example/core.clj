(ns example.core
  (:require [scrap.core :as scrap]
            [scrap.rules :as rules]
            [browser.core :as browser])
  (:gen-class))


(defn test-scrap []
  (let [url "https://github.com/kokosro/scrap"
        response (browser/doget url)
        forms (scrap/extract rules/forms (response :body))]
    forms))

