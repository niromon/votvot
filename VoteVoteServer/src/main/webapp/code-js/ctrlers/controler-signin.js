votvot.controller('SigninController', function ($scope, $modal, $window,$location, $http, usSpinnerService) {
	$scope.master = {};

	$scope.creds= {};$scope.creds.email="joris@deguet.org";$scope.creds.password="password";
	
	$scope.signin = function(user) {
		usSpinnerService.spin('spinner-1');
		console.log(user);
		$http.post('/rest/social/signin', user).
		success(function(data, status, headers, config) {
			console.log(data);
			$window.localStorage.token = data.id;
			window.localStorage['token'] = data.id;
			window.localStorage['personID'] = data.userID;
			console.log("Success "  + $window.localStorage.token);
			usSpinnerService.stop('spinner-1');
			$location.path("/home");
		}).
		error(function(data, status, headers, config) {
			console.log("Error " + data );
			usSpinnerService.stop('spinner-1');
			var modalInstance = $modal.open({
				templateUrl: 'myModalContent.html',
				controller: 'SigninController',
				resolve: {
					items: function () {
						return [1,2,3];
					}
				}
			});

			modalInstance.result.then(function (selectedItem) {
				$scope.selected = selectedItem;
			}, function () {
				console.log('Modal dismissed at: ' + new Date());
			});
			delete $window.localStorage.token;
		});
		$scope.master = angular.copy(user);
	};

	$scope.reset = function() {
		$scope.user = angular.copy($scope.master);
	};

	$scope.reset();
});


votvot.controller('SignupController', function ($scope, $window,$location, $http) {
	$scope.master = {};

	$scope.signup = function(user) {
		console.log("this is signup");
		console.log(user);
		user.adress = {lat:45.0, lng:45.0, description:'ici'};
		user.birthPlace = {lat:45.0, lng:45.0, description:'ici'};
		//user.birthDate = user.date;
		// user.hashed = CryptoJS.SHA256(user.password);
		$http.post('/rest/social/signup', user).
		success(function(data, status, headers, config) {
			console.log("Success "  );
			console.log(data);
			$location.path("/signin");
			//usSpinnerService.stop('spinner-1');
		}).
		error(function(data, status, headers, config) {
			console.log("Error " + data );
			//usSpinnerService.stop('spinner-1');
		});
		$scope.master = angular.copy(user);
	};


	$scope.reset = function() {
		$scope.user = angular.copy($scope.master);
	};

	$scope.reset();
});
