(ns example.core
  (:require [scrap.core :as scrap]
            [scrap.rules :as rules]
            [clojure.string :as s]
            [browser.core :as browser])
  (:gen-class))


(defn test-scrap []
  (let [url "https://github.com/kokosro/scrap"
        response (browser/doget url)
        forms (scrap/extract rules/forms (response :body))]
    forms))


(def urls (atom {}))
(def url-names (atom {}))
(defn get-url-name [url]
  (get @url-names url))
(defn save-to-file [file-name content]
  (spit (str "files/" file-name) content))

(defn add-url [parent url name]
  (if-not (contains? @urls url)
    (do 
      (swap! url-names assoc url name)
      (swap! urls assoc (browser/normalize-url parent url) false))))
(defn mark-done [url]
  (swap! urls assoc url true))
(defn get-next-url []
  ;(println "getting next url")
  (loop [the-urls @urls]
    (let [next-url (first the-urls)]
      (if-not (second next-url)
        (do
        ;  (println (first next-url))
          (if-not (nil? (first next-url))
            (first next-url)
            (recur (rest the-urls))))
        (recur (rest the-urls))))))

(defn count-next-url []
  (loop [the-urls @urls
         c 0]
    (if (= (count the-urls) 0)
      c
      (let [next-url (first the-urls)]
        (if-not (second next-url)
          (recur (rest the-urls) c)
          (recur (rest the-urls) (inc c)))))))


(defn parse-url [url & args]
  (let [response (browser/doget url)
        links (if (= (:content-type response) "text/plain")
                []
                (doall (map (fn [link] {:url (browser/normalize-url (str url "/") (:link (:value (:url link))))
                                        :name (:name (:value (:url link)))}) (scrap/extract rules/links (response :body)))))]
    (mark-done url)
    (println url (:content-type response))
    (save-to-file (get-url-name url) (:body response))
    (doall (map #(add-url url (:url %) (:name %)) links))
    (if-not args 
      (future 
        (Thread/sleep 20)
        (parse-url (get-next-url)))
      links)))


(def files-folder (clojure.java.io/file "files/"))

(defn next-file [folder how-many]
  (take how-many (file-seq folder)))

(defn read-file [file]
  (println file)
  (try (with-open [rdr (clojure.java.io/reader file)]
         (loop [content ""]
           (let [line (line-seq rdr)]
             (if line
               (recur (str content "\n" (s/join " " line)))
               content))))
    (catch Exception e "")))

(defn read-files [folder number-of] 
  (s/join "\n\n" (doall (map #(read-file %) (next-file folder number-of)))))



;(s/split (read-files files-folder 10) #" ")
