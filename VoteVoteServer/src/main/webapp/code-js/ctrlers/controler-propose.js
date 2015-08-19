

votvot.controller('ProposeController', ['$scope','$location', '$http', function ($scope,$location, $http) {
	$scope.master = {};

	$scope.poll = {};
	$scope.poll.choices = [];
	$scope.poll.links = [];

//	$scope.signin = function(user) {
//		console.log(user);
//		$http.post('/rest/social/signin', user).
//		success(function(data, status, headers, config) {
//			console.log("Success "  );
//			console.log(data);
//		}).
//		error(function(data, status, headers, config) {
//			console.log("Error " + data );
//		});
//		$scope.master = angular.copy(user);
//	};

	$scope.reset = function() {
		$scope.user = angular.copy($scope.master);
	};

	$scope.addChoice = function(choice) {
		if (choice === "" || $scope.poll.choices.indexOf(choice) > -1) return;
		$scope.poll.choices.push(choice);
		$( "#newOne" ).focus();
		$( "#newOne" ).val("");
		//alert($scope.poll.choices);
	};

	$scope.delChoice = function(choice) {
		var index =  $scope.poll.choices.indexOf(choice);
		if (index > -1) {
			$scope.poll.choices.splice(index, 1);
		}
	};

//	$scope.addLink = function(link) {
//		if (link === ""  || $scope.poll.links.indexOf(link) > -1) return;
//		$scope.poll.links.push(link);
//		//alert($scope.poll.choices);
//	};
//
//	$scope.delLink = function(link) {
//		var index =  $scope.poll.links.indexOf(link);
//		if (index > -1) {
//			$scope.poll.links.splice(index, 1);
//		}
//	};

	$scope.propose = function(poll) {
		console.log("About to propose");
		console.log(poll);
		$http.post('/rest/poll/propose', poll).
		success(function(data, status, headers, config) {
			console.log("Success "  );
			console.log(data);
			$location.path( "/promote/"+data.id );
		}).
		error(function(data, status, headers, config) {
			console.log("Error " + data );
			alert("Error  "  + data);
		});
	};

	$scope.setShowChoices = function(show){
		console.log("option changed " +show);
		$scope.showChoices = !(show === 'YesNo');
	}

	$scope.reset();
}]);

