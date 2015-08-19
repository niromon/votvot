votvot.controller('MyVotesController', function ($window,$scope, $http , $routeParams) {
	var id = "";
	angular.copy($window.localStorage['personID'], id);
	$http.get('/rest/vote/votesby/'+$window.localStorage['personID'], id).
	success(function(data, status, headers, config) {
		console.log("Success for fetching poll"  );
		console.log(data);
		$scope.votes = data;
	}).
	error(function(data, status, headers, config) {
		console.log("Error " + data );
	});
	console.log('/rest/poll/supportsby/'+$window.localStorage['personID']);
	
	$http.get('/rest/poll/supportsby/'+$window.localStorage['personID'], id).
	success(function(data, status, headers, config) {
		console.log("Success for fetching supports"  );
		console.log(data);
		$scope.supports = data;
	}).
	error(function(data, status, headers, config) {
		console.log("Error " + data );
	});

});

