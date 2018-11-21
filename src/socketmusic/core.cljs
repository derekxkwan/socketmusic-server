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

(defn start-app []
  (let [app (ep)]
    (if (not (nil? page-dir))
      (do (.use app (ep.static. page-dir))
          (println "hosting webpage located at" page-dir))
      (do (.use app (ep.static. (str js/__dirname "/")))
          (println "hosting webpage located at" (str js/__dirname "/")))
      )
    (.listen app server-port (fn [] (println "starting app server on port" (str server-port))))
    )
  )

(defn start-ws [cur-http]
  (println "starting websockets")
  (sio cur-http)
  )

(defn osc-router [msg rinfo]
  (let [address (first msg)
        args (vec (rest msg))]
    (cond
      (= address "/log") (println (array args))
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
