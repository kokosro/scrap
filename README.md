# scrap

A Clojure library for extracting informations from a HTML page - uses enlive
Provides a scraping library and a library for fetching content from urls that uses clj-http
## Usage

[![Clojars Project](http://clojars.org/org.clojars.kokos/scrap/latest-version.svg)](http://clojars.org/org.clojars.kokos/scrap)

scrap.core library contains macros for creating rules for extractions.
```clojure

(refer 'scrap.core :only '[an a defrule])
(refer 'scrap.extractors :only '[attribut content attributs data-attrs])

(defrule resources 
  (an url [:a] {:link (attribut url :href)
                :data (data-attrs url)
                :name (content url)})
  (an url [:link] {:link (attribut url :href)
                   :data (attributs url #"data\-(.*)")
                   :name (attribut url :rel)})
  (an url [:script] {:link (attribut url :src)
                     :data (data-attrs url)
                     :name (attribut url :type)}))
```

Above we're creating a rule for extracting resources from three different trags :a :link :sript
There are some predefined rules defined in scrap.rules

```clojure
(ns example.core
  (:require [scrap.core :as scrap]
            [scrap.rules :as rules]
            [browser.core :as browser])
  (:gen-class))


(defn test-scrap []
  (let [url "https://github.com/kokosro/scrap"
        response (browser/doget url) ;; fetching urls
        links (scrap/extract rules/resources (response :body))
        forms (scrap/extract rules/forms (response :body))
        tables (scrap/extract rules/tables (response :body))
        articles (scrap/extract rules/articles (response :body))
        base-url (scra[/extract rules/base-url (response :body)])]
    links))
```
calling
```clojure
(text-scrap)
```
returns
```clojure
(
{:url {:value {:link "#start-of-content", :data {}, :name "Skip to content"}, :content ()}} 
{:url {:value {:link "https://github.com/", :data {:ga-click nil}, :name ""}, :content ()}} 
{:url {:value {:link "/personal", :data {:ga-click nil, :selected-links nil}, :name "Personal"}, :content ()}} 
{:url {:value {:link "/open-source", :data {:ga-click nil, :selected-links nil}, :name "Open source"}, :content ()}} 
{:url {:value {:link "/business", :data {:ga-click nil, :selected-links nil}, :name "Business"}, :content ()}}

 ;and many more.....
 )
```

Use of [[org.clojars.kokos/browser "0.1.0"]](https://clojars.org/org.clojars.kokos/browser) is volunary. 
***Any html string would do*** as the second argument of [scrap.core/extract](https://github.com/kokosro/scrap/blob/master/src/scrap/core.clj#L69)


## License



Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
