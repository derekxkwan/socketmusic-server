(ns socketmusic.core)

(def osc (js/require "node-osc"))
(def sio (js/require "socket.io"))
(def ep (js/require "express"))

(def server-port 3000)
(def osc-in-port 33333)
(def osc-out-port 11111)
(def res-dir (aget (.-argv js/process) 2)) ;;resource directory passed as arg
(defonce io (atom nil))
(defonce http-server (atom nil))
(defonce osc-server (atom nil))
(defonce osc-client (atom nil))

(defn start-app []
  (println "starting main server")
  (let [app (ep)]
    (.get app "/" (fn [req res] (.send res "Hello, world!")))
    (.listen app server-port (fn [] (println "listening on port " (str server-port))))
    )
  )

(defn start-ws [cur-http]
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
    cur-server)

  )

(defn start-osc-client []
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
