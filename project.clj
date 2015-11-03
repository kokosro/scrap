(defproject org.clojars.kokos/scrap "0.1.0"
  :description "Extracting information from HTML resources"
  :url "https://github.com/kokosro/scrap"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :deploy-repositories [["clojars" {:url "https://clojars.org/repo/"
                   :sign-releases false}]]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-http "1.1.1"]
                 [crouton "0.1.2"]
                 [uri "1.1.0"]
                 [clj-time "0.8.0"]
                 [enlive "1.1.5"]
                 [slingshot "0.12.2"]
                 [cheshire "5.3.1"]])
