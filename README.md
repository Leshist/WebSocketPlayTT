# how to use

run server with sbt run
localhost:9000/ws2 -websocket with    SameOrigin Security check
localhost:9000/ws  -websocket without SameOrigin Security check

localhost:9000 launches js app that opens websocket and sends some of the messages to websocket
* ping 
* unsupported $type 
* without $type request
* login wrong credentials
* login valid credentials
* subscribe to tables
* add table
* update table
* invalid update table
* remove table
* invalid remove table
* unsub
* add table
* update table
* invalid update table
* remove table
* invalid remove table
                   
you can check code at public/javascripts/angularApp.js