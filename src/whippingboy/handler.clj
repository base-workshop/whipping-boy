(ns whippingboy.handler
  (:require [clojure.walk :refer [keywordize-keys]]
            [compojure.core :refer [ANY
                                    DELETE
                                    GET
                                    HEAD
                                    OPTIONS
                                    POST
                                    PUT
                                    context
                                    defroutes]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [whippingboy.data :as data]
            [whippingboy.http :as http]
            [whippingboy.middleware :refer [wrap-exception-handler
                                        wrap-request-logger
                                        wrap-response-logger]]
            [ring.middleware.format-response :refer [wrap-restful-response]]
            [ring.middleware.json :refer [wrap-json-body]]))

(defroutes api-routes
  "Main client API route definitions"
  (context "/api" []
           (OPTIONS "/" []
                    (http/options [:options] {:version "0.3.0-SNAPSHOT"}))
           (ANY "/" []
                (http/method-not-allowed [:options]))
           (context "/websites" []
                    (GET "/" []
                         (http/ok (data/get-websites-with-icons)))
                    (GET "/:id" [id]
                         (http/not-implemented))
                    (HEAD "/:id" [id]
                          (http/not-implemented))
                    (POST "/" [:as req]
                          (let [website (data/create-website (keywordize-keys (req :body)))
                                location (http/url-from req (str (website :id)))]
                            (http/created location website)))
                    (PUT "/:id" [id]
                         (http/not-implemented))
                    (DELETE "/:id" [id]
                            (http/not-implemented))
                    (OPTIONS "/" []
                             (http/options [:options :get :head :put :post :delete]))
                    (ANY "/" []
                         (http/method-not-allowed [:options :get :head :put :post :delete])))

           (context "/artists" []
                    (GET "/" []
                         (http/ok (data/get-artists)))
                    (GET "/:id" [id]
                         (if-let [data (data/get-artist (Integer. id))]
                           (http/ok data)
                           (http/not-found))))

           (context "/tracks" []
                    (GET "/" []
                         (http/ok (data/get-tracks)))
                    (GET "/:id" [id]
                         (if-let [data (data/get-track (Integer. id))]
                           (http/ok data)
                           (http/not-found))))

           (context "/covers" []
                    (GET "/" []
                         (http/ok (data/get-covers)))
                    (GET "/:id" [id]
                         (if-let [data (data/get-cover (Integer. id))]
                           (http/ok data)
                           (http/not-found))))

           (context "/labels" []
                    (GET "/" []
                         (http/ok (data/get-labels)))
                    (GET "/:id" [id]
                         (if-let [data (data/get-label (Integer. id))]
                           (http/ok data)
                           (http/not-found))))

           (context "/albums" []
                    (GET "/" []
                         (http/ok (data/get-albums)))
                    (GET "/:id" [id]
                         (if-let [data (data/get-albums (Integer. id))]
                           (http/ok data)
                           (http/not-found))))

  (route/not-found "Nothing to see here, move along now")))

(def app
  "Application entry point & handler chain"
  (->
   (handler/api api-routes)
   (wrap-json-body)
   (wrap-request-logger)
   (wrap-exception-handler)
   (wrap-response-logger)
   (wrap-restful-response)))
