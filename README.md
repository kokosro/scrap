# scrap

A Clojure library for extracting informations from a HTML page - uses enlive
Provides a scraping library and a library for fetching content from urls that uses clj-http
## Usage

[![Clojars Project](http://clojars.org/org.clojars.kokos/scrap/latest-version.svg)](http://clojars.org/org.clojars.kokos/scrap)

scrap.core library contains macros for creating rules for extractions.
```clojure
(require 'scrap.core)
(require 'scrap.extrctors)
(scrap.core/defrule resource 
  (scrap.core/an url [:a] {:link (scrap.extractors/atribut url :href)
                      :name (scrap.extractors/content url)})
  (scrap.core/an url [:link] {:link (scrap.extractors/atribut url :href)
                   :name (scrap.extractors/atribut url :rel)})
  (scrap.core/an url [:script] {:link (scrap.extractors/atribut url :src)
                     :name (scrap.extractors/atribut url :type)}))
```

Above we're creating a rule for extracting resources from three different trags :a :link :sript
There are some predefined rules defined in scrap.rules

```clojure
(ns example.core
  (:require [scrap.core :as scrap]
            [browser.core :as browser])
  (:gen-class))


(defn test-scrap []
  (let [url "https://github.com/kokosro/scrap"
        response (browser/doget url) ;; fetching urls
        links (scrap/extract resource (response :body))]
    links))
```
response:
```clojure
({:url {:content (), :value {:link "#start-of-content", :name "Skip to content"}}} 
 {:url {:content (), :value {:link "https://github.com/", :name ""}}} 
 {:url {:content (), :value {:link "/join", :name "Sign up"}}} 
 {:url {:content (), :value {:link "/login?return_to=%2Fkokosro%2Fscrap", :name "Sign in"}}}
 ;and many more.....
 )
```




## License



Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
