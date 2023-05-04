(function( $ ) {

	var defaults = {
		
	};

    $.fn.numberSelector = function(options) {

    	return this.each(function() {
    	
	    	var self = this;
	    	var $this = $(this);
	    	var opts = $.extend( {}, defaults, options );
	
	    	var broadcastValueChange = function() {
	    		var value = $(this).val();
	    		$this.trigger( "valueChange", [ value, this, opts ] );
	    	}

	    	$this.find('input').on('click', broadcastValueChange);
	    	
	    	$this.find('form')[0].getValues = function() {
	    		var $form = $(this);
	    		return [$form.find('input').val()];
	    	};
	    	
	    	$this.find('form')[0].setValues = function(values) {
	    		var $form = $(this);
	    		if ('undefined' != typeof(values[0])) {
	    			$form.find('input').val(values[0]).click();
	    		}
	    	}
	    	
    	});	
	    	
    };

}( jQuery ));