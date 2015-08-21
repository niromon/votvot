votvot.controller('PollController', function ($window,$scope,  $location, $http , $routeParams) {
	var id = "";
	angular.copy($routeParams.pollId, id);
	console.log('/rest/poll/'+$routeParams.pollId);
	
	// GET THE POLL TO DISPLAY
	$http.get('/rest/poll/'+$routeParams.pollId, id).
	success(function(data, status, headers, config) {
		console.log("Success for fetching poll"  );
		console.log(data);
		$scope.poll = data;
		$scope.candidates = data.choices;
	}).
	error(function(data, status, headers, config) {
		console.log("Error " + data );
	});

	// SEE IF THIS USER ALREADY VOTED
	var hasvotedurl = '/rest/vote/hasvoted/'+$routeParams.pollId+'/'+$window.localStorage.personID;
	console.log("URL " + hasvotedurl);
	$http.get(hasvotedurl, id).
	success(function(data, status, headers, config) {
		console.log("Success for fetching has voted "  );
		console.log(data);
		$scope.hasvoted = data;
	}).
	error(function(data, status, headers, config) {
		console.log("Error " + data );
	});
	
	$scope.buckets = [];
	$scope.buckets[0] = [];
	//$scope.buckets[1] = [];
	console.log("buckets");
	console.log($scope.buckets);
	console.log($scope.candidates);
//	$scope.candidates = [
//	                     { 'title': 'John', 'drag': true },
//	                     { 'title': 'Paul', 'drag': true },
//	                     { 'title': 'George', 'drag': true },
//	                     { 'title': 'Ringo', 'drag': true }
//	                     ];
	console.log($scope.candidates);
	this.dropCallback = function() {
		console.log("callback");
		for (var i = 0 ; i < $scope.buckets.length ; i++){
			var bucket = $scope.buckets[i];
			if (bucket.length == 0){
				$scope.buckets.splice(i, 1);
			}
		}
		if ($scope.buckets[$scope.buckets.length-1].length !=0){
			$scope.buckets.push([]);
		}
	};
	$scope.sendVote = function(poll, choice) {
		  var voteRequest = {'questionId':$routeParams.pollId,'voterId': $window.localStorage.personID, 'choice':choice};
		  $http.post('/rest/vote/vote', voteRequest).
			success(function(data, status, headers, config) {
				console.log("Success "  );
				console.log(data);
				$location.path("/result/"+poll.id);
			}).
			error(function(data, status, headers, config) {
				console.log("Error " + data );
			}
		  );
	}
	$scope.sendPrefVote = function() {
		  var votes = angular.copy($scope.buckets, votes);
		  votes.splice(votes.length-1,1);
		  console.log(votes);
		  var vote = votes.map(function(bucket) { return bucket.map(function(item) { return item; }).join('='); }).join('>');
		  vote = angular.toJson(votes);
		  var voteRequest = {'questionId':$routeParams.pollId,'voterId': $window.localStorage.personID, 'choice':vote};
		  console.log("Vote is " + vote);
		  $http.post('/rest/vote/vote', voteRequest).
			success(function(data, status, headers, config) {
				console.log("Success "  );
				console.log(data);
				$location.path("/result/"+$routeParams.pollId);
			}).
			error(function(data, status, headers, config) {
				console.log("Error " + data );
			}
		  );
	}
	
	$scope.support = function(poll, choice) {
		  var supportRequest = {'questionId':$routeParams.pollId,'voterId': $window.localStorage.personID, 'opinion':choice};
		  $http.post('/rest/poll/support', supportRequest).
			success(function(data, status, headers, config) {
				console.log("Success "  );
				console.log(data);
				$window.location.reload();
			}).
			error(function(data, status, headers, config) {
				console.log("Error " + data );
			}
		  );
	}
});

