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
	    $('#upload-job').modal({
	        closeHTML: "<a href='#' title='Close' class='modal-close'>x</a>",
	        position: ["20%",],
	        containerId: 'confirm-container',
	        containerCss: {
	          'height': '220px',
	          'width': '565px'
	        },
	        onShow: function (dialog) {
	          var modal = this;
            $("#errorMsg").hide();
	          // if the user clicks "yes"
	          //$('.yes', dialog.data[0]).click(function () {
	            // close the dialog
	            //modal.close(); // or $.modal.close();
	          //});
	        }
	      });
	},
	render: function() {
	}
});

var uploadView;
azkaban.UploadJobView= Backbone.View.extend({
	events : {
		"change #file": "handleFileChange",
		"click #deploy-btn": "handleUploadJob"
	},
	initialize : function(settings) {
    $("#errorMsg").hide();
	},
	handleUploadJob : function(evt) {
	  // First make sure we can upload

	  var projectName = $('#path').val();
	  var dir = document.getElementById('file').value;
	  if (projectName == "") {
	    $("#errorMsg").text("ERROR: Empty Project Name.");
	    $("#errorMsg").slideDown("fast");
	  }
	  else if (dir == "") {
	    $("#errorMsg").text("ERROR: No zip file selected.");
      $("#errorMsg").slideDown("fast");
	  }
	  else {
	     $("#errorMsg").slideUp("fast");
      var lastIndexOfForwardSlash = dir.lastIndexOf('\\');
      var lastIndexOfBackwardSlash = dir.lastIndexOf('/');
      
      var startIndex = Math.max(lastIndexOfForwardSlash, lastIndexOfBackwardSlash);
      startIndex += 1;
      var filename = dir.substring(startIndex);
  		$.ajax({
  		  type: 'POST',
  		  url: contextURL + "/manager",
  		  async: false,
  		  cache: false,
  		  data: {action: "verify", project: projectName, filename: filename },
  		  success: function(data) {
  		    if (data.status == "error") {
  		       $("#errorMsg").text("ERROR: " + data.message);
  		       $("#errorMsg").slideDown("fast");
  		    }
  		    else {
  		       
  		    }
  		  }
  		});
		}
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