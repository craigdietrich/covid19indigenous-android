(function( $ ) {

	var defaults = {
	};

    $.fn.intermission = function(options, callback) {

    	return this.each(function() {
    	
	    	var self = this;
	    	var $this = $(this);
	    	var opts = $.extend( {}, defaults, options );

	    	var getLocation = function(callback) {
	    		if (navigator.geolocation) {
	    			navigator.geolocation.getCurrentPosition(callback);
	    			return true;
	    		}
	    		return false;
	    	}
	    	
	    	$this.find('[name="locationButton"]').click(function() {
	    		getLocation(function(position) {
	    			var lat = position.coords.latitude;
	    			var lng = position.coords.longitude;
	    			$this.find('[name="location"]').val(lat + ', ' + lng);
	    		})
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