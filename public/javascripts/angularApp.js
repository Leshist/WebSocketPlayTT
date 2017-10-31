var wsApp = angular.module('wsApp', []);
wsApp.controller('wsController', ['$scope','$http', function($scope, $http){

  $scope.init = function() {
//    $scope.wsUrl = "ws:localhost:9000/ws"
    $scope.wsUrl = "ws:localhost:9000/ws2"
    $scope.sleep = function (seconds)
                   {
                     var e = new Date().getTime() + (seconds * 1000);
                     while (new Date().getTime() <= e) {}
                   }

    $scope.pingRequest = {
        "$type": "ping",
        "seq": 1
      }

    $scope.someRequest = {
            "$type": "some",
            "seq": 1
          }

    $scope.withoutTypeRequest = {
                "$stype": "some",
                "seq": 1
              }

    $scope.loginTrueRequest = {
                    "$type": "login",
                    "username": "user1234",
                    "password": "password1234"
                  }

    $scope.loginFalseRequest = {
                        "$type": "login",
                        "username": "Donald",
                        "password": "Trump"
                      }

    $scope.subRequest = {
                            "$type": "subscribe_tables"
                          }


    $scope.addTableRequest = {
                                "$type": "add_table",
                                "after_id": 1,
                                "table": {
                                  "name": "table - Foo Fighters",
                                  "participants": 4
                                }
                              }

    $scope.updateTableRequest = {
                                  "$type": "update_table",
                                  "table": {
                                    "id": 3,
                                    "name": "table - Foo Fighters",
                                    "participants": 4
                                           }
                                }

    $scope.updateFailTableRequest = {
                                      "$type": "update_table",
                                      "table": {
                                        "id": 9000,
                                        "name": "table - Foo Fighters",
                                        "participants": 4
                                               }
                                    }

    $scope.removeTableRequest = {
                                  "$type": "remove_table",
                                  "id": 3
                                }

    $scope.removeFailTableRequest = {
                                      "$type": "remove_table",
                                      "id": 90000
                                    }

    $scope.unsubTablesRequest = {
                                  "$type": "unsubscribe_tables"
                                }





    console.log("trying to open new ws")
    $scope.ws = new WebSocket($scope.wsUrl);
    console.log("trying to open new ws2")

    $scope.ws.onopen = function(){
                   console.log("Socket has been opened!");
                   $scope.sleep(2)

                   $scope.ws.send(JSON.stringify($scope.pingRequest));
                   $scope.ws.send(JSON.stringify($scope.someRequest));
                   $scope.ws.send(JSON.stringify($scope.withoutTypeRequest));
                   $scope.ws.send(JSON.stringify($scope.loginFalseRequest));
                   $scope.ws.send(JSON.stringify($scope.loginTrueRequest));
                   $scope.ws.send(JSON.stringify($scope.subRequest));
                   $scope.ws.send(JSON.stringify($scope.addTableRequest));
                   $scope.ws.send(JSON.stringify($scope.updateTableRequest));
                   $scope.ws.send(JSON.stringify($scope.updateFailTableRequest));
                   $scope.ws.send(JSON.stringify($scope.removeTableRequest));
                   $scope.ws.send(JSON.stringify($scope.removeFailTableRequest));

                   // now unsub and send same update, add, remove msgs
                   $scope.ws.send(JSON.stringify($scope.unsubTablesRequest));
                   $scope.ws.send(JSON.stringify($scope.addTableRequest));
                   $scope.ws.send(JSON.stringify($scope.updateTableRequest));
                   $scope.ws.send(JSON.stringify($scope.updateFailTableRequest));
                   $scope.ws.send(JSON.stringify($scope.removeTableRequest));
                   $scope.ws.send(JSON.stringify($scope.removeFailTableRequest));

               };

    $scope.ws.onmessage = function(message) {
                   console.log(message)
               };
  };



  $scope.init();
}]);