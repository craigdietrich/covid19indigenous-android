(function( $ ) {

	var defaults = {
	};

    $.fn.latlngSelector = function(options, callback) {

    	return this.each(function() {
    	
	    	var self = this;
	    	var $this = $(this);
	    	var opts = $.extend( {}, defaults, options );

	    	var getLocation = function(callback) {
	    		
	    		var showError = function(error) {
	    			  var error = '';
	    			  switch(error.code) {
	    			    case error.PERMISSION_DENIED:
	    			    	error = "User denied the request for Geolocation."
	    			      break;
	    			    case error.POSITION_UNAVAILABLE:
	    			    	error = "Location information is unavailable."
	    			      break;
	    			    case error.TIMEOUT:
	    			    	error = "The request to get user location timed out."
	    			      break;
	    			    case error.UNKNOWN_ERROR:
	    			    	error = "An unknown error occurred."
	    			      break;
	    			  }
	    			  $this.find('.latlngError').text(error);
	    		};
	    		
	    		if (navigator.geolocation) {
	    			navigator.geolocation.getCurrentPosition(callback, showError);
	    			return true;
	    		}
	    		return false;
	    	}
	    	
	    	$this.find('[name="locationButton"]').click(function() {
	    		var loc = getLocation(function(position) {
	    			var lat = position.coords.latitude;
	    			var lng = position.coords.longitude;
	    			$this.find('[name="latitude"]').val(lat);
	    			$this.find('[name="longitude"]').val(lng);
	    		});
	    	});
	    	
	    	$this.find('form')[0].getValues = function() {
	    		var $form = $(this);
	    		var values = [];
	    		$form.find('input, select').each(function() {
	    			values.push($(this).val());
	    		});
	    		return values;
	    	};
	    	
	    	$this.find('form')[0].setValues = function(values) {
	    		var $form = $(this);
	    		$form.find('input, select').each(function(index) {
	    			var value = values[index];
	    			if (null == value) return;
	    			$(this).val(value).click();
	    		});
	    		return values;
	    	};
   			
    	});	
	    	
    };

}( jQuery ));