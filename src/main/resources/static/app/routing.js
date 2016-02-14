app.config(function($stateProvider, $urlRouterProvider) {
	//
	// For any unmatched url, redirect to /defects
	$urlRouterProvider.otherwise("/latest");
	//
	// Now set up the states
	$stateProvider.state('latest', {
		url : "/latest?fromcur&tocur",
		templateUrl : "partials/latest.html"
	}).state('chart', {
		url : "/chart?fromcur&tocur",
		templateUrl : "partials/chart.html"
	});
});
