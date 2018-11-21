# socketmusic-server v 0.01
server for the socketmusic series of projects (such as (socketMusic:wireless)[https://derekxkwan.com/musicart/socketMusic.html])

## about
abstracting out the server from my previous socketmusic:wireless project and moving to a new build system of **ClojureScript** compiled by **shadow-cljs** to run on **node.js**


- to install shadow-cljs: `npm install -g shadow-cljs`

## npm dependencies
- `express`
- `socket.io`
- `node-osc`

## usage
- hosted osc server is at port `33333`
- outgoing osc messages are on port `11111`
- website is hosted on port `3000`
- the root directory of the hosted website is passed as the **first arg**
  - defaults to `default-page/` folder
- running the app: `node socketmusic.js [webpage dir]`

## osc-messages
format for osc messages sent to the osc server
- `/debug args` prints the args to the console
- `/client-list` sends out base64 client list on osc output port on address `/client-list`
- `/num-clients` sends the number of clients on osc output port on address `/num-clients`
- `/ws/all [args]` broadcasts array of args to all clients
- `/ws/client-idx [client-idx] [args]` - sends array of args to client at given index of client-list
- `/ws/client [client] [args]` - sends array of args to specified (in base64) client
- `/ws/rand-client [args]` - sends array of args to random client

## build
- compile: `shadow-cljs compile app`
- watch: `shadow-cljs watch app`
- build: `shadow-cljs build app`
- 
## license
gpl v 3
