$.namespace('azkaban');

var jobView;
azkaban.JobView= Backbone.View.extend({
	events : {
		"click #upload-btn":"handleUploadJob"
	},
	initialize : function(settings) {

	},
	handleUploadJob : function(evt) {
		console.log("click upload");
	},
	render: function() {
	}
});

var uploadView;
azkaban.UploadJobView= Backbone.View.extend({
	events : {
		"onchange #file": "handleFileChange"
	},
	initialize : function(settings) {

	},
	handleUploadJob : function(evt) {
		
	},
	handleFileChange : function(evt) {
		var path = $('#path');
		if(path.val() == '') {
			var dir = document.getElementById('file').value;
			var lastIndexOf = dir.lastIndexOf('.');
			var lastIndexOfForwardSlash = dir.lastIndexOf('\\');
			var lastIndexOfBackwardSlash = dir.lastIndexOf('/');
			
			var startIndex = Math.max(lastIndexOfForwardSlash, lastIndexOfBackwardSlash);
			startIndex += 1;
			path.val(dir.substring(startIndex, lastIndexOf));
		}
	},
	render: function() {
	}
});

$(function() {
	jobView = new azkaban.JobView({el:$( '#all-jobs-content' )});
	uploadView = new azkaban.UploadJobView({el:$('#upload-job')});
});