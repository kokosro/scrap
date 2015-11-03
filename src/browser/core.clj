(ns browser.core
  (:require [clj-http.cookies])
  (:require [clj-http.client :as client])
  (:require [uri.core :as u])
  (:require [clojure.string :as s])
  (:use [slingshot.slingshot :only [try+]])
  (:require [browser.header :as header])
  (:gen-class))

(defn build-response [url response]
  {:url url
   :url-info (u/uri->map (u/make url))
   :status (:status response)
   :redirects (:trace-redirects response)
   :content-type (header/extract-name (:headers response) "Content-Type")
   :content-type-info (header/extract-info (:headers response) "Content-Type")
   :response-time (:request-time response)
   :content-size (header/extract-value (:headers response) "Content-Length")
   :error 0
   :body (:body response)})

(defn can-fetch-url? [url & {:keys [accepted-schemes] :or {accepted-schemes ["http" "https"]}}]
  (if (and 
        (not (= "" url))
        (not (nil? url))
        (u/absolute? (u/make url)))
    (let [url-map (u/uri->map (u/make url))]
      (reduce (fn [r x]
                (or r
                    (= x (:scheme url-map)))) false accepted-schemes))
    false))

(defn- remove-url-fragments [url]
  (if-not (nil? url)
    (first (s/split url #"#"))
    nil))

(defn normalize-url [base-url url]
  (if-not (nil? url)
    (try
      (let [x (remove-url-fragments (.toString (u/resolve (u/make base-url) (u/make (s/replace url #" " "")))))]
        (if (can-fetch-url? x)
          x
          base-url))
      (catch Exception e base-url))
    base-url))


(defn- not-a-valid-url-response [url]
  {:body ""
   :error 600
   :status 0
   :content-type ""
   :content-type-info ""
   :response-time ""
   :content-size 0
   :redirects []
   :url url})

(defn- request-handler [fetching-fn args]
  (let [fetched-response (try+ (let [response (fetching-fn args)] response)
                               (catch Object _ _))]
    (build-response (:url args) fetched-response)))

(defn create []
  (let [cookie-store (clj-http.cookies/cookie-store)]
    (fn [url & {:keys [method params agent headers]
                :or {method :get
                     params nil
                     agent "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"
                     headers {}}}]
      (if (can-fetch-url? url)
        (request-handler client/request (merge {:method method
                                 :url url
                                 :insecure? true
                                 :follow-redirects true
                                 :max-redirects 10
                                 :socket-timeout 2000 :conn-timeout 2000
                                 :headers (merge {"User-Agent" agent} headers)}
                                (if-not (nil? params)
                                  (if (= :get method)
                                    {:query-params params}
                                    {:form-params params}))))
        (not-a-valid-url-response url)))))

(def ^:dynamic *client* (create))

(defn doget [url & params]
  (*client* url :method :get :params (first params)))
(defn dopost [url & params]
  (*client* url :method :post :params (first params)))
(defn doreq [url method & params]
  (*client* url :method (keyword method) :params (first params)))


(defmacro with-client [client & body]
  `(binding [*client* ~client]
     ~@body))