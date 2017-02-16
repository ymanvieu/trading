app.config(function(RestangularProvider) {
	// add a response interceptor
	RestangularProvider.addResponseInterceptor(function(data, operation, what, url, response, deferred) {
		var extractedData;
		// Maps response depending on URL
		if (operation === "getList"
				&& (url === "/rate/latest")) {
			extractedData = [];
			extractedData.content = data.content;
			extractedData.totalPages = data.totalPages;
			extractedData.totalElements = data.totalElements;
			extractedData.size = data.size;
		} else {
			extractedData = data;
		}
		return extractedData;
	});
});

// We are using Restangular here, the code bellow will just make an ajax request
app.factory('api', function(Restangular) {
	return {
		rate : {
			latest : function(query) {
				return Restangular.all('rate/latest').getList(query);
			},
			raw : function(query) {
				return Restangular.all('rate/raw').getList(query);
			},
		},
	};
});