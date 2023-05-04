(function( $ ) {

	var defaults = {
		
	};

    $.fn.textSelector = function(options) {

    	return this.each(function() {
    	
	    	var self = this;
	    	var $this = $(this);
	    	var opts = $.extend( {}, defaults, options );
	
	    	var broadcastValueChange = function() {
	    		var value = $(this).val();
	    		$this.trigger( "valueChange", [ value, this, opts ] );
	    	}

	    	$this.find('textarea').on('click', broadcastValueChange);
	    	
	    	$this.find('form')[0].getValues = function() {
	    		var $form = $(this);
	    		return [$form.find('textarea').val()];
	    	};
	    	
	    	$this.find('form')[0].setValues = function(values) {
	    		var $form = $(this);
	    		if ('undefined' != typeof(values[0])) {
	    			$form.find('textarea').val(values[0]).click();
	    		}
	    	}
	    	
    	});	
	    	
    };

}( jQuery ));