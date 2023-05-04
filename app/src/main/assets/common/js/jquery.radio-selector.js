(function( $ ) {

	var defaults = {
		
	};

    $.fn.radioSelector = function(options) {

    	return this.each(function() {
    	
	    	var self = this;
	    	var $this = $(this);
	    	var opts = $.extend( {}, defaults, options );
	
	    	var broadcastValueChange = function() {
	    		var name = $(this).attr('name');
	    		var value = $('input[name="'+name+'"]:checked').val();
	    		if ('undefined' == typeof(value)) return;
	    		$this.trigger( "valueChange", [ value, this, opts ] );
	    	}
	    	
	    	broadcastValueChange();
	    	$this.find('input[type="radio"]').on('click', broadcastValueChange);
	    	
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