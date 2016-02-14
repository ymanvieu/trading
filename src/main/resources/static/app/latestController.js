app.controller('latestController', function($scope, api, $stateParams) {
	$scope.totalPages = 0;
	
	$scope.headers = [ {
		title : 'From',
		value : 'fromcur'
	}, {
		title : 'To',
		value : 'tocur'
	}, {
		title : 'Value',
		value : 'value'
	}, {
		title : 'Date',
		value : 'date'
	}];
	
	// default criteria that will be sent to the server
	$scope.filterCriteria = {
		pageNumber : 1,
		sortDir : 'desc',
		sortedBy : 'date',
		fromcur : $stateParams.fromcur,
		tocur : $stateParams.tocur,
	};

	// The function that is responsible of fetching the result from the server
	// and setting the grid to the new result
	$scope.fetchResult = function() {
		return api.rate.latest($scope.filterCriteria).then(function(data) {
			$scope.rates = data.content;
			$scope.totalPages = data.totalPages;
			$scope.ratesCount = data.totalElements;
			$scope.itemsPerPage= data.size;
		}, function() {
			$scope.defects = [];
			$scope.totalPages = 0;
		});
	};

	// called when navigate to another page in the pagination
	$scope.selectPage = function(page) {
		$scope.filterCriteria.pageNumber = page;
		$scope.fetchResult();
	};

	// Will be called when filtering the grid, will reset the page number to one
	$scope.filterResult = function() {
		$scope.filterCriteria.pageNumber = 1;
		$scope.fetchResult().then(function() {
			$scope.current = 1;
		});
	};

	// called when clicking on any field to sort
	$scope.onSort = function(sortedBy, sortDir) {
		$scope.filterCriteria.sortDir = sortDir;
		$scope.filterCriteria.sortedBy = sortedBy;
		$scope.filterCriteria.pageNumber = 1;
		
		$scope.fetchResult().then(function() {
			$scope.current = 1;
		});
	};

	$scope.open = function($event, opened) {
		$event.preventDefault();
		$event.stopPropagation();

		$scope[opened] = true;
	};

	$scope.dateOptions = {
		formatYear : 'yy',
		startingDay : 1
	};

	// manually select a page to trigger an ajax request to populate the grid on page load
	$scope.selectPage(1);
});
