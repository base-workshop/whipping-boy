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
                    (DELETE "/:id" [id]
                            (http/ok (data/delete-website (Integer. id))))
                    (GET "/:id" [id]
                         (http/not-implemented))
                    (HEAD "/:id" [id]
                          (http/not-implemented))
                    (POST "/" [:as req]
                          (let [website (data/create-website (keywordize-keys (req :body)))
                                location (http/url-from req (str (website :id)))]
                            (http/created location website)))
                    (PUT "/:id" {body :body params :params}
                         (let [update (data/update-website (Integer. (:id params)) (keywordize-keys body))
                               website (data/get-website (Integer. (:id params)))
                               location (http/url-from body (str (website :id)))]
                           (if (pos? update)
                             (http/created location website)
                             (http/not-updated))))
                    (DELETE "/:id" [id]
                            (http/not-implemented))
                    (OPTIONS "/" []
                             (http/options [:options :get :head :put :post :delete]))
                    (ANY "/" []
                         (http/method-not-allowed [:options :get :head :put :post :delete])))

           (context "/artists" []
                    (GET "/" []
                         (http/ok (data/get-artists)))
                    (DELETE "/:id" [id]
                            (http/ok (data/delete-artist (Integer. id))))
                    (POST "/" [:as req]
                          (let [artist (data/create-artist (keywordize-keys (req :body)))
                                location (http/url-from req (str (artist :id)))]
                            (http/created location artist)))
                    (PUT "/:id" {body :body params :params}
                         (let [update (data/update-artist (Integer. (:id params)) (keywordize-keys body))
                               artist (data/get-artist (Integer. (:id params)))
                               location (http/url-from body (str (artist :id)))]
                           (if (pos? update)
                             (http/created location artist)
                             (http/not-updated))))
                    (GET "/:id" [id]
                         (if-let [data (data/get-artist (Integer. id))]
                           (http/ok data)
                           (http/not-found))))

           (context "/tracks" []
                    (GET "/" []
                         (http/ok (data/get-tracks)))
                    (DELETE "/:id" [id]
                            (http/ok (data/delete-track (Integer. id))))
                    (POST "/" [:as req]
                          (let [track (data/create-track (keywordize-keys (req :body)))
                                location (http/url-from req (str (track :id)))]
                            (http/created location track)))
                    (PUT "/:id" {body :body params :params}
                         (let [update (data/update-track (Integer. (:id params)) (keywordize-keys body))
                               track (data/get-track (Integer. (:id params)))
                               location (http/url-from body (str (track :id)))]
                           (if (pos? update)
                             (http/created location track)
                             (http/not-updated))))
                    (GET "/:id" [id]
                         (if-let [data (data/get-track (Integer. id))]
                           (http/ok data)
                           (http/not-found))))

           (context "/covers" []
                    (GET "/" []
                         (http/ok (data/get-covers)))
                    (DELETE "/:id" [id]
                            (http/ok (data/delete-cover (Integer. id))))
                    (POST "/" [:as req]
                          (let [cover (data/create-cover (keywordize-keys (req :body)))
                                location (http/url-from req (str (cover :id)))]
                            (http/created location cover)))
                    (PUT "/:id" {body :body params :params}
                         (let [update (data/update-cover (Integer. (:id params)) (keywordize-keys body))
                               cover (data/get-cover (Integer. (:id params)))
                               location (http/url-from body (str (cover :id)))]
                           (if (pos? update)
                             (http/created location cover)
                             (http/not-updated))))
                    (GET "/:id" [id]
                         (if-let [data (data/get-cover (Integer. id))]
                           (http/ok data)
                           (http/not-found))))

           (context "/labels" []
                    (GET "/" []
                         (http/ok (data/get-labels)))
                    (DELETE "/:id" [id]
                            (http/ok (data/delete-label (Integer. id))))
                    (POST "/" [:as req]
                          (let [label (data/create-label (keywordize-keys (req :body)))
                                location (http/url-from req (str (label :id)))]
                            (http/created location label)))
                    (PUT "/:id" {body :body params :params}
                         (let [update (data/update-label (Integer. (:id params)) (keywordize-keys body))
                               label (data/get-label (Integer. (:id params)))
                               location (http/url-from body (str (label :id)))]
                           (if (pos? update)
                             (http/created location label)
                             (http/not-updated))))
                    (GET "/:id" [id]
                         (if-let [data (data/get-label (Integer. id))]
                           (http/ok data)
                           (http/not-found))))

           (context "/albums" []
                    (GET "/" []
                         (http/ok (data/get-albums)))
                    (DELETE "/:id" [id]
                            (http/ok (data/delete-album (Integer. id))))
                    (POST "/" [:as req]
                          (let [album (data/create-album (keywordize-keys (req :body)))
                                location (http/url-from req (str (album :id)))]
                            (http/created location album)))
                    (PUT "/:id" {body :body params :params}
                         (let [update (data/update-album (Integer. (:id params)) (keywordize-keys body))
                               album (data/get-album (Integer. (:id params)))
                               location (http/url-from body (str (album :id)))]
                           (if (pos? update)
                             (http/created location album)
                             (http/not-updated))))
                    (GET "/:id" [id]
                         (if-let [data (data/get-albums (Integer. id))]
                           (http/ok data)
                           (http/not-found))))

           (context "/teams" []
                    (GET "/" []
                         (http/ok (data/get-teams)))
                    (DELETE "/:id" [id]
                            (http/ok (data/delete-team (Integer. id))))
                    (GET "/all" []
                         (http/ok (data/get-all-teams)))
                    (POST "/" [:as req]
                          (let [team (data/create-team (keywordize-keys (req :body)))
                                location (http/url-from req (str (team :id)))]
                            (http/created location team)))
                    (GET "/:id" [id]
                         (if-let [data (data/get-team (Integer. id))]
                           (http/ok data)
                           (http/not-found)))))

  (route/not-found "Nothing to see here, move along now"))

(def app
  "Application entry point & handler chain"
  (->
   (handler/api api-routes)
   (wrap-json-body)
   (wrap-request-logger)
   (wrap-exception-handler)
   (wrap-response-logger)
   (wrap-restful-response)))
