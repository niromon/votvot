
votvot.controller('ResultController', function ($scope, $http , $routeParams) {
	
	
	var id = "";
	angular.copy($routeParams.pollId, id);
	$scope.scores = [];
	// get the poll
	$http.get('/rest/poll/'+$routeParams.pollId, id).
	success(function(data, status, headers, config) {
		$scope.poll = data;
		// get the actual results if available   TODO PREF VERSUS SINGLE
		$http.get('/rest/vote/results/'+$routeParams.pollId, id).
		success(function(data, status, headers, config) {
			console.log("Success for results"  );
			console.log(data);
			if ($scope.poll.type === 'SingleChoice'){
				console.log("single choice");
				var datapoints = [];
				$scope.scores = [];
				for (var key in data.result){
					console.log(key+" "+data.result[key])
					var point = { "label":key, "y":data.result[key] };
					console.log(point);
					datapoints.push( point);
					$scope.scores.push({"option":key,"score":data.result[key]});
				}
				console.log(datapoints);
				var chart = new CanvasJS.Chart("canvas-chart", {
					data: [//array of dataSeries              
					       { //dataSeries object
					    	   /*** Change type "column" to "bar", "area", "line" or "pie"***/
					    	   type: "pie",
					    	   dataPoints: datapoints
					       }		
					       ]
				});
				chart.render();
			}else{
				console.log("preferential");
				// we have to show the graph
				$scope.results = data;
				var nodes = [];
				var edges = [];
				var graph = data.edges;
				var id = 1;
				for (var i = 0 ; i < graph.length ; i++){
					var triplet = graph[i];
					if (nodes.indexOf(triplet.one) < 0) nodes.push(triplet.one);
					if (nodes.indexOf(triplet.two) < 0) nodes.push(triplet.two);
				}
				console.log("Candidates " + nodes);
				var visNodes = [];
				for (var i = 0 ; i < nodes.length ; i++){
					visNodes.push({id: i, label: nodes[i]});
				}
				console.log("VIS nodes " + visNodes);
				var dataForVis = new vis.DataSet(visNodes);
//				
				var visEdges = [];
				for (var i = 0 ; i < graph.length ; i++){
					var triplet = graph[i];
					visEdges.push({from :  nodes.indexOf(triplet.one),  to :  nodes.indexOf(triplet.two), arrows:'to'});
				}
				var edgesForVis = new vis.DataSet(visEdges);

				var container = document.getElementById('mynetwork');
				var dataVis = {
						nodes: dataForVis,
						edges: edgesForVis
				};
				var options = {};
				var network = new vis.Network(container, dataVis, options);
			}
			
		}).
		error(function(data, status, headers, config) {
			alert("Error " + data );
			console.log("Error " + data );
		});
	}).
	error(function(data, status, headers, config) {
		alert("Error " + data );
		console.log("Error " + data );
	});

	
	console.log('/rest/vote/results/'+$routeParams.pollId);
	
	// get the participation data and draw the graph
	$http.get('/rest/vote/participation/daily/'+$routeParams.pollId, id).
	success(function(data, status, headers, config) {
		console.log("Success for participations "  + data );
		console.log(data);
		var datapoints = [];
		for (var key in data){
			console.log(key+" "+data[key])
			var point = { "x":new Date(key), "y":data[key] };
			console.log(point);
			datapoints.push( point);
		}
		console.log(datapoints);
		var chart = new CanvasJS.Chart("canvas-participation", {
			title:{  text: "Participation  " },
			data: [//array of dataSeries              
			       { //dataSeries object
			    	   /*** Change type "column" to "bar", "area", "line" or "pie"***/
			    	   type: "line",
			    	   dataPoints: datapoints
			       }		
			       ]
		});
		chart.render();
	}).
	error(function(data, status, headers, config) {
		console.log("Error " + data );
	});

});

