(ns socketmusic.core)

(def osc (js/require "node-osc"))
(def sio (js/require "socket.io"))
(def ep (js/require "express"))

(def server-port 3000)
(def osc-in-port 33333)
(def osc-out-port 11111)
(def page-dir (aget (.-argv js/process) 2)) ;;resource directory passed as arg
(defonce io (atom nil))
(defonce http-server (atom nil))
(defonce osc-server (atom nil))
(defonce osc-client (atom nil))

(def clients (atom {}))

(defn provide-client-list []
  (mapv name (keys @clients)))

(defn provide-num-clients []
  (count (keys @clients)))

(defn get-client-by-idx [idx]
  (get (provide-client-list) idx))

(defn send-client-list []
  (.send @osc-client "/client-list" (apply array (provide-client-list))))

(defn send-num-clients []
  (.send @osc-client "/num-clients" (provide-num-clients)))

(defn start-app []
  (let [app (ep)]
    (if (not (nil? page-dir))
      (do (.use app (ep.static. page-dir))
          (println "hosting webpage located at" page-dir))
      (do (.use app (ep.static. (str js/__dirname "/default-page/")))
          (println "hosting webpage located at" (str js/__dirname "/default-page/")))
      )
    (.listen app server-port (fn [] (println "starting app server on port" (str server-port))))
    )
  )

(defn socket-callbacks [socket]
  (.on socket "connection"
       (fn [client]
         (swap! clients assoc (keyword (.-id client)) client)
         (.on client "disconnect"
              (fn []
                (swap! clients dissoc (keyword (.-id client)))
                )
              ))
       )
  )
         

(defn ws-broadcast [args]
  (println (str "broadcasting " args))
  (let [address (first args)
        cur-args (apply array (rest args))]
    (.emit @io address cur-args)
    )
  )
  

(defn ws-send [target args]
  ;(println (str "sending " args " to " target))
  (let [address (first args)
        cur-args (apply array (rest args))]
    (.emit (.to @io target) address cur-args)
    )
  )

(defn start-ws [cur-http]
  ;(println "starting websockets")
  (let [ws-server (sio cur-http)]
    (socket-callbacks ws-server)
    ws-server)
  )

(defn osc-router [msg rinfo]
  ;(println msg)
  (let [address (first msg)
        args (rest msg)]
    (cond
      (= address "/debug") (println args)
      (= address "/client-list") (send-client-list)
      (= address "/num-clients") (send-num-clients)
      (= address "/ws/all") (ws-broadcast args)
      (= address "/ws/client-idx") (ws-send (get-client-by-idx (first args)) (rest args))
      (= address "/ws/client") (ws-send (first args) (rest args))
      (= address "/ws/rand-client") (ws-send (get-client-by-idx (rand-int (provide-num-clients))) args)
      :else nil)
    )
  )

(defn start-osc-server []
  (let [cur-server (osc.Server. osc-in-port "0.0.0.0")]
    (.on cur-server "message" osc-router)
      (println "starting osc server on port" osc-in-port)
    cur-server)

  )

(defn start-osc-client []
  (println "osc outgoing on port" osc-out-port)
  (osc.Client. "127.0.0.1" osc-out-port))

(defn start! []
  (reset! http-server (start-app))
  (reset! io (start-ws @http-server))
  (reset! osc-server (start-osc-server))
  (reset! osc-client (start-osc-client))
  )

(defn stop! []
  (.close @http-server)
  (.kill @osc-client)
  (.kill @osc-server)
  (reset! http-server nil)
  )

(defn main []
  (start!))
