//TODO http://stackoverflow.com/questions/12506329/how-to-dynamically-change-header-based-on-angularjs-partial-view

//Angular module for votvot

//http://www.pluralsight.com/courses/angularjs-patterns-clean-code
//https://github.com/johnpapa/angularjs-styleguide

var votvot = angular.module('votvot', [
                                       'ngRoute','ui.bootstrap','votvot.i18n','angularSpinner','ngDragDrop'
                                       ]);

//Define Routing for app
//Uri /AddNewOrder -> template add_order.html and Controller AddOrderController
votvot.config(['$routeProvider',
               function($routeProvider) {
	$routeProvider.
	when('/signin', {
		templateUrl: 'angular-templates/signin.html',
		controller: 'SigninController'
	}).
	when('/signup', {
		templateUrl: 'angular-templates/signup.html',
		controller: 'SignupController'
	}).
	when('/poll/:pollId', {
		templateUrl: 'angular-templates/poll.html',
		controller: 'PollController'
	}).
	when('/person/:personId', {
		templateUrl: 'angular-templates/person.html',
		controller: 'PersonController'
	}).
	when('/result/:pollId', {
		templateUrl: 'angular-templates/results.html',
		controller: 'ResultController'
	}).
	when('/home', {
		templateUrl: 'angular-templates/home.html',
		controller: 'MainController'
	}).
	when('/propose', {
		templateUrl: 'angular-templates/propose.html',
		controller: 'ProposeController'
	}).
	when('/promote/:pollId', {
		templateUrl: 'angular-templates/promote.html',
		controller: 'PromoteController'
	}).
	when('/results', {
		templateUrl: 'angular-templates/results.html',
		controller: 'ResultsController'
	}).
	when('/myvotes', {
		templateUrl: 'angular-templates/myvotes.html',
		controller: 'MyVotesController'
	}).
	otherwise({
		redirectTo: '/home'
	});
}]);

votvot.config(['usSpinnerConfigProvider', function (usSpinnerConfigProvider) {
	usSpinnerConfigProvider.setDefaults({color: 'green'});
}]);

votvot.factory('authInterceptor', function ($rootScope, $q, $window) {
	return {
		request: function (config) {
			config.headers = config.headers || {};
			if ($window.localStorage.token) {
				config.headers.Authorization = 'Bearer ' + $window.localStorage.token;
			}
			return config;
		},
		response: function (response) {
			if (response.status === 401) {
				// handle the case where the user is not authenticated
			}
			return response || $q.when(response);
		}
	};
});

votvot.run( function($rootScope, $window, $location) {

	// register listener to watch route changes
	$rootScope.$on( "$routeChangeStart", function(event, next, current) {
		if ( typeof $window.localStorage.personID != 'undefined' ) {
			console.log("Logged as " + $window.localStorage.personID);
		} 
		else if ( next.templateUrl == "angular-templates/signup.html" ) {
			// you can signup
			console.log("Not Logged going to signup " +$window.localStorage.personID);
		}
		else{
			// if not logged, redirect for all other pages
			$location.path( "/signin" );
			console.log("Not Logged " +$window.localStorage.personID);
		}
	})
});



votvot.controller('LocaleController', function ($scope, $translate) {
	$scope.changeLanguage = function (key) {
		$translate.use(key);
	};
});



votvot.controller('TopNavController', function($window,$http ,$location, $scope) {
	console.log("TOPNAV: Token is :"  + window.localStorage['token']);
	console.log(window.localStorage['token']);
	if (window.localStorage['token'] != undefined){
		console.log('/rest/social/returnin/'+window.localStorage['token']);
		$http.get('/rest/social/returnin/'+window.localStorage['token']).
		success(function(data, status, headers, config) {
			console.log(data);
			$scope.token = window.localStorage['token'];
			$scope.personID = data.id;
		}).
		error(function(data, status, headers, config) {
			console.log("Error " + data );
			delete window.localStorage['token'];
			delete $window.localStorage.token;
			delete $window.localStorage.personID;
			$location.path("/signin");
		});
	}

	$scope.signout = function() {
		console.log("this is signout");
		// call to server to disable cookie and token id
		$http.post('/rest/social/signout', $window.localStorage.token).
		success(function(data, status, headers, config) {
			console.log("Success for signout"  );
			console.log(data);
		}).
		error(function(data, status, headers, config) {
			console.log("Error " + data );
		});
		// delete from local storage too
		delete $window.localStorage.token;
		delete $scope.token;
		delete $scope.personID;
		delete $window.localStorage.personID;
		$location.path("/");
	};

});


votvot.controller('MainController', function($window, $http, $scope) {
	// initial retrievals to populate
	$http.get('/rest/poll/questions').
	success(function(data) {
		$scope.polls = data;
	});
	//$http.get('/rest/pollproposition/all').
	$http.get('/rest/poll/propositions').
	success(function(data) {
		$scope.propositions = data;
	});
	
	$http.get('/rest/poll/globalnumbers').
	success(function(data) {
		console.log(data);
		$scope.numbers = data;
	});

	$http.get('/rest/social/idchecks').
	success(function(data) {
		$scope.checks = data;
	});

	$scope.vote = function(poll, answer){
		console.log("vote for "+poll.question + "   is  " + answer);
		console.log("logged as token "+ $window.localStorage['token']);
		console.log("logged as id    "+ $window.localStorage['personID']);
		var id  = $window.localStorage['personID'];
		request = {voterId:id, questionId:poll.id, choice:answer};
		console.log(request);
		$http.post('/rest/vote/vote', request).
		success(function(data, status, headers, config) {
			console.log("Success "  );
			$scope.person = data;
			console.log(data);
		}).
		error(function(data, status, headers, config) {
			console.log("Error " + data );
		}
		);
	}
});
