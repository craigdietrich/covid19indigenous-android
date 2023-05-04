(function( $ ) {

	var defaults = {
		
	};

    $.fn.likertSelector = function(options) {

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
	    	
	    	$this.find('input[type="radio"]').on('click', broadcastValueChange);
	    	
	    	$this.find('form')[0].getValues = function() {
	    		var $form = $(this);
	    		var values = [];
	    		$form.find('tbody tr').each(function() {
	    			var value = $(this).find('input:checked').val();
	    			if ('undefined' == typeof(value)) value = null;
	    			values.push(value);
	    		});
	    		return values;
	    	};
	    	
	    	$this.find('form')[0].setValues = function(values) {
	    		var $form = $(this);
	    		$form.find('tbody tr').each(function(index) {
	    			var value = values[index];
	    			if (null == value) return;
	    			$(this).find('input[value="'+value+'"]').click();
	    		});
	    		return values;
	    	}
	    	
    	});	
	    	
    };

}( jQuery ));