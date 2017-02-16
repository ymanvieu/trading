//http://jsfiddle.net/gh/get/jquery/1.9.1/highslide-software/highcharts.com/tree/master/samples/stock/demo/basic-line/
//http://jsfiddle.net/pablojim/Hjdnw/
//http://www.highcharts.com/demo/line-time-series
app.controller('chartController', function($scope, $filter, api, $stateParams, $window, $q) {

	$scope.noupdate = true;
	$scope.last;

	$scope.filterCriteria = {
		tocur : $stateParams.tocur,
		fromcur : $stateParams.fromcur,
		startDate : null,
		endDate : null
	};

	$scope.updateTitle = function(from, to, date, value) {
		$scope.chartConfig.title.text = 'Latest ' + from + '/' + to + " - " + $filter('date')(date, "yyyy-MM-dd HH:mm:ss") + " : " + value;
		// console.log($scope.chartConfig.title.text);
	};
	
	Highcharts.setOptions({
		global : {
			timezoneOffset : (new Date()).getTimezoneOffset()
		}
	});

	$scope.chartConfig = {
		options : {
			chart : {
				type : 'line',
				zoomType : 'x'
			},
			navigator : {
				enabled : true,
				adaptToUpdatedData : false,
				series : {
					data : []
				}
			},
			rangeSelector : {
				buttons : [ {
					type : 'day',
					count : 1,
					text : '1d'
				}, {
					type : 'week',
					count : 1,
					text : '1w'
				}, {
					type : 'month',
					count : 1,
					text : '1m'
				}, {
					type : 'month',
					count : 3,
					text : '3m'
				}, {
					type : 'month',
					count : 6,
					text : '6m'
				}, {
					type : 'ytd',
					text : 'YTD'
				}, {
					type : 'year',
					count : 1,
					text : '1y'
				}, {
					type : 'all',
					text : 'All'
				} ]
			}
		},
		series : [ {
			name : $scope.filterCriteria.fromcur + '/' + $scope.filterCriteria.tocur,
			data : []
		} ],
		dataGrouping : {
			enabled : false
		},
		title : {
			text : ''
		},
		loading : false,
		useHighStocks : true,
		xAxis : {
			type : 'datetime',
			ordinal : false,
			events : {
				//afterSetExtremes : $scope.afterSetExtremes
			}
		}
	};
	
	$scope.chart = function() {
		return $scope.chartConfig.getHighcharts();
	};

	$q.all([api.rate.raw($scope.filterCriteria), api.rate.latest($scope.filterCriteria)]).then(function(dataAll) {
		
		console.debug("done all");
		
		var data = dataAll[0];
		var datalast = dataAll[1];
		
		$scope.last = datalast.content[0];
			
		result = $scope.last;
		data.push([ result.date, result.value ]);
		$scope.updateTitle(result.fromcur.code, result.tocur.code, result.date, result.value);
	
		
		$scope.chartConfig.series[0].data = data;
		$scope.chartConfig.options.navigator.series.data = data;
		
		$scope.filterCriteria.startDate = $filter('date')(data[0][0], "yyyy-MM-ddTHH:mm:ss.sssZ");
		$scope.filterCriteria.endDate = $filter('date')(data[data.length-1][0], "yyyy-MM-ddTHH:mm:ss.sssZ");
		
		$scope.processLast($scope.last);

		$scope.chartConfig.xAxis.events.afterSetExtremes = function(e) {
			if ($scope.noupdate) {
				$scope.noupdate = false;
				return;
			}
	
			var eMin = $filter('date')(e.min, "yyyy-MM-ddTHH:mm:ss.sssZ");
			var eMax = $filter('date')(e.max, "yyyy-MM-ddTHH:mm:ss.sssZ");
			
			$scope.filterCriteria.startDate = eMin;
			$scope.filterCriteria.endDate = eMax;
			$scope.fetchResult();
		};
	});

	$scope.fetchResult = function() {
		$scope.chartConfig.loading = 'Loading data from server...';

		api.rate.raw($scope.filterCriteria)
		.then(function(data) {
			$scope.chartConfig.series[0].data = data;
		}).finally(function() {
			$scope.chartConfig.loading = false;
		});
	};

	$scope.processLast = function(rate) {
			$scope.updateTitle(rate.fromcur.code, rate.tocur.code, rate.date, rate.value);
			$scope.last = rate;

			$scope.chart().series[0].addPoint([ rate.date, rate.value ]);
			$scope.chartConfig.options.navigator.series.data.push([ rate.date, rate.value ]);
			$scope.noupdate = true;
			$scope.chart().zoomOut();
	};

	var stompClient = Stomp.over(new SockJS('/stomp'));
	// stompClient.debug = null;

	stompClient.connect({}, function(frame) {
		// console.log("[STOMP] Connect: " + frame);
		stompClient.subscribe('/topic/latest/' + $scope.filterCriteria.fromcur + '/' + $scope.filterCriteria.tocur, function(frame) {
			var scope = angular.element(document.getElementById('chart1')).scope();
			scope.$apply(function() {
				scope.processLast(JSON.parse(frame.body));
			});
		});
	}, function(error) {
		// console.log("[STOMP] Connect: Error->" + error);
	});

	$scope.$on('$locationChangeStart', function(event) {
		stompClient.disconnect(function() {
			// console.log("[STOMP] Disconnected");
		});
	});
});