(ns scrap.rules
  (:require [scrap.core :as scrap])
  (:require [scrap.extractors :as extract])
  (:gen-class))

(refer 'scrap.core :only '[an a defrule])
(refer 'scrap.extractors :only '[attribut content attributs data-attrs])



(defrule resources
  (an url [:a] {:link (attribut url :href)
                :data (data-attrs url)
                :name (content url)})
  (an url [:link] {:link (attribut url :href)
                   :data (data-attrs url)
                   :name (attribut url :rel)})
  (an url [:script] {:link (attribut url :src)
                     :data (data-attrs url)
                     :name (attribut url :type)}))


(defrule links
  (an url [:a] {:link (attribut url :href)
                :data (data-attrs url)
                :class (attribut url :class)
                :alt (attribut url :alt)
                :name (content url)}))

(defrule articles
  (an article [:article.post] 
      {:data (data-attrs article)}
      :with [(a title [:header.entry-header :.entry-title :a] (content title))
             (an author [:.entry-meta :.author :a] (content author))
             (a paragraph [:.entry-content :p] (content paragraph))]))

(defrule base-url
  (an url [:base] (attribut url :href)))

(defrule forms
  (a form [:form]
     {:method (attribut form :method)
      :data (data-attrs form)
      :name (attribut form :name)
      :class (attribut form :class)
      :action (attribut form :action)}
     :with [(an input [:input] {:type (attribut input :type)
                                :name (attribut input :name)
                                :id (attribut input :id)
                                :value (attribut input :value)
                                :checked (attribut input :checked)})
            (a textarea [:textarea] {:name (attribut textarea :name)
                                     :value (content textarea)
                                     :id (attribut textarea :id)})
            (a select [:select] {:name (attribut select :name)
                                 :id (attribut select :id)}
               :with [(an option [:option] {:value (attribut option :value)
                                            :selected (attribut option :selected)
                                            :label (content option)})])]))


(defrule tables
  (a table [:table]
     {:class (attribut table :class)
      :data (data-attrs table)
      :id (attribut table :id)}
     :with [(a head [:thead]
               {:class (attribut head :class)}
               :with [(a row [:tr]
                         {:data (data-attrs row)}
                         :with [(a column [:th]
                                   (content column))])])
            (a body [:tbody]
               {:class (attribut body :class)
                :data (data-attrs body)}
               :with [(a row [:tr]
                         {:class (attribut row :class)
                          :data (data-attrs row)}
                         :with [(a column [:td]
                                   (content column))])])
            (a footer [:tfoot]
               {:class (attribut footer :class)}
               :with [(a row [:tr]
                         {:class (attribut row :class)}
                         :with [(a column [:td]
                                   (content column))])])]))