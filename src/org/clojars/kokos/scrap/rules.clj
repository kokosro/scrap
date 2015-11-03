(ns org.clojars.kokos.scrap.rules
  (:require [scrap.core :as scrap])
  (:require [scrap.extractors :as extract])
  (:gen-class))

(scrap/defrule resource 
  (scrap/an url [:a] {:link (extract/atribut url :href)
                      :name (extract/content url)})
  (scrap/an url [:link] {:link (extract/atribut url :href)
                   :name (extract/atribut url :rel)})
  (scrap/an url [:script] {:link (extract/atribut url :src)
                     :name (extract/atribut url :type)}))
(comment
  (scrap/an url [:img] {:link (extract/atribut url :src)
                  :name (extract/atribut url :alt)}))

(scrap/defrule links
  (scrap/an url [:a] {:link (extract/atribut url :href)
                      :class (extract/atribut url :class)
                      :alt (extract/atribut url :alt)
                      :name (extract/content url)}))

(scrap/defrule articles
  (scrap/an article [:article.post] {}
            :with [(scrap/an title [:header.entry-header :.entry-title :a] (extract/content title))
                   (scrap/an author [:.entry-meta :.author :a] (extract/content author))
                   (scrap/an paragraph [:.entry-content :p] (extract/content paragraph))]))


(scrap/defrule base-url
  (scrap/an url [:base] (extract/atribut url :href)))

(scrap/defrule forms
  (scrap/an form [:form]
            {:method (extract/atribut form :method)
             :name (extract/atribut form :name)
             :class (extract/atribut form :class)
             :action (extract/atribut form :action)}
            :with [(scrap/an input [:input] {:type (extract/atribut input :type)
                                             :name (extract/atribut input :name)
                                             :id (extract/atribut input :id)
                                             :value (extract/atribut input :value)
                                             :checked (extract/atribut input :checked)})
                   (scrap/an textarea [:textarea] {:name (extract/atribut textarea :name)
                                                   :value (extract/content textarea)
                                                   :id (extract/atribut textarea :id)})
                   (scrap/an select [:select] {:name (extract/atribut select :name)
                                               :id (extract/atribut select :id)}
                             :with [(scrap/an option [:option] {:value (extract/atribut option :value)
                                                                :selected (extract/atribut option :selected)
                                                                :label (extract/content option)})])]))