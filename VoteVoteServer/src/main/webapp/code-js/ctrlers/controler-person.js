votvot.controller('PersonController', function ($window,$scope, $http , $routeParams) {
	var id = "";
	angular.copy($routeParams.personId, id);
	console.log('/rest/social/person/'+$routeParams.personId);
	
	$http.get('/rest/social/person/'+$routeParams.personId).
	success(function(data, status, headers, config) {
		console.log("Success "  );
		$scope.person = data;
		console.log(data);
	}).
	error(function(data, status, headers, config) {
		console.log("Error " + data );
	}
	);
});

