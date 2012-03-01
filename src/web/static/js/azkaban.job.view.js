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

azkaban.UploadJobView= Backbone.View.extend({
	events : {
	},
	initialize : function(settings) {

	},
	handleUploadJob : function(evt) {
		
	},
	render: function() {
	}
});

$(function() {
	jobView = new azkaban.JobView({el:$( '#all-jobs-content' )});
});