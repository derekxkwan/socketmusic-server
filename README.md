# socketmusic-server
server for the socketmusic series of project

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
  - **js** files should be in `js` dir
  - **css** files should be in `css` dir
  - **resources** should be the `res` dir
- running the app: `node socketmusic.js [webpage dir]`

## build
- compile: `shadow-cljs compile app`
- watch: `shadow-cljs watch app`
- build: `shadow-cljs build app`

## status
under construction...
