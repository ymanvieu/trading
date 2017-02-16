app.directive('sortBy', function() {
	return {
		templateUrl : 'sort-by.html',
		restrict : 'E',
		transclude : true,
		replace : true,
		scope : {
			sortdir : '=',
			sortedby : '=',
			sortvalue : '@',
			onsort : '='
		},
		link : function(scope, element, attrs) {
			scope.sort = function() {
				if (scope.sortedby == scope.sortvalue)
					scope.sortdir = scope.sortdir == 'asc' ? 'desc' : 'asc';
				else {
					scope.sortedby = scope.sortvalue;
					scope.sortdir = 'asc';
				}
				scope.onsort(scope.sortedby, scope.sortdir);
			}
		}
	};
});
