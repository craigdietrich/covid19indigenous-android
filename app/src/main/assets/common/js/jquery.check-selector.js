(function( $ ) {

	var defaults = {
		
	};

    $.fn.checkSelector = function(options) {

    	return this.each(function() {
    	
	    	var self = this;
	    	var $this = $(this);
	    	var opts = $.extend( {}, defaults, options );
	    	
	    	$this.find('input[type="checkbox"]').on('click', function() {
	    		var value = $(this).val();
	    		if ('undefined' == typeof(value)) return;
	    		$this.trigger( "valueChange", [ value, this, opts ] );	
	    	});
	    	
	    	$this.find('form')[0].getValues = function() {
	    		var $form = $(this);
	    		var values = [];
	    		$form.find('input:checked').each(function() {
	    			values.push($(this).val());
	    		});
	    		$form.find('input[name="other"]').each(function() {
	    			value = $(this).val();
	    			if (value.length) values.push(value);
	    		});
	    		return values;
	    	};
	    	
	    	$this.find('form')[0].setValues = function(values) {
	    		var $form = $(this);
	    		for (var j = 0; j < values.length; j++) {
	    			$el = $form.find('input[value="'+values[j]+'"]');
	    			if ($el.length) {
	    				$el.click();
	    			} else {
	    				$form.find('input[name="other"]').val(values[j]);
	    			}
	    		}
	    	}
	    	
    	});	
	    	
    };

}( jQuery ));