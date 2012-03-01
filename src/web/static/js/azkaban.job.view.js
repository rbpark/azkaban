$.namespace('azkaban');

var jobView;
azkaban.JobView= Backbone.View.extend({
  events : {
    "click #upload-btn":"handleUploadJob"
  },
  initialize : function(settings) {

  },
  handleUploadJob : function(evt) {
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

        // if the user clicks "yes"
        $('.yes', dialog.data[0]).click(function () {
          // close the dialog
          modal.close(); // or $.modal.close();
        });
      }
    });
  },
  render: function() {
  }
});

$(function() {
  jobView = new azkaban.JobView({el:$( '#all-jobs-content' )});
});