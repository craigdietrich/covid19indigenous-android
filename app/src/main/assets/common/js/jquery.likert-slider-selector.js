(function( $ ) {

	var defaults = {
		
	};

    $.fn.likertSliderSelector = function(options) {

    	return this.each(function() {
    	
	    	var self = this;
	    	var $this = $(this);
	    	var opts = $.extend( {}, defaults, options );
	
	    	var doRangeColorsAndLabels = function() {
	    		$this.find('input[type="range"]').each(function() {
		    		var $range = $(this);
					var value = $range.val();
					var prompts = $range.data('prompts').split(';');
					var prompt = prompts[parseInt(value) - 1];
					$range.removeClass('range-zero').removeClass('range-low').removeClass('range-med').removeClass('range-neu');
					if (value == 0) {
						$range.addClass('range-zero');
						prompt = 'No answer';
					} else if (value <= prompts.length / 2) {
						$range.addClass('range-low');
					} else if (value == 3) {
						$range.addClass('range-med');
					} else if (value == 6) {
						$range.addClass('range-neu');
						prompt = "Don't know / Not applicable";
						$range.closest('tr').find('input[type="radio"]').prop('checked', true);
					}
					$range.nextAll('b').text(prompt);
	    		});
	    	};
	    	
	    	var broadcastValueChange = function() {
	    		$this.trigger( "valueChange", [ '', this, opts ] );
	    	}
	    	
	    	doRangeColorsAndLabels();
	    	broadcastValueChange();
	    	$this.find('input[type="range"]').on('input', function() {
	    		var $range = $(this);
	    		$range.closest('tr').find('input[type="radio"]').prop('checked', false);
	    	});
	    	$this.find('input[type="radio"]').on('click', function() {
	    		var $radio = $(this);
	    		$radio.closest('tr').find('input[type="range"]').val(6); 
	    	});
	    	$this.find('input[type="range"]').on('input', doRangeColorsAndLabels); 
	    	$this.find('input[type="range"]').on('input', broadcastValueChange);
	    	$this.find('input[type="radio"]').on('click', doRangeColorsAndLabels); 
	    	$this.find('input[type="radio"]').on('click', broadcastValueChange);
	    	
	    	$this.find('form')[0].getValues = function() {
	    		var $form = $(this);
	    		var values = [];
	    		$form.find('input[type="range"]').each(function() {
	    			var $this = $(this);
	    			var value = $this.val();
	    			if ($this.closest('tr').find('input[value="dk"]').is(':checked')) value = "6";  // TODO: magic number
	    			if ($this.closest('tr').find('input[value="na"]').is(':checked')) value = "7";  // TODO: magic number
	    			values.push(value);
	    		});
	    		return values
	    	};
	    	
	    	$this.find('form')[0].setValues = function(values) {
	    		var $form = $(this);
	    		$form.find('input[type="range"]').each(function(index) {
	    			var $this = $(this);
	    			var value = values[index];
	    			if (null == value) return;
	    			if (6 == parseInt(value)) {
	    				$this.closest('tr').find('input[value="dk"]').prop('checked', true);  // TODO: magic number
	    			} else if (7 == parseInt(value)) {
	    				$this.closest('tr').find('input[value="na"]').prop('checked', true);  // TODO: magic number
	    			} else {
	    				$this.val(value).trigger('input');
	    			}
	    		});
	    	}
	    	
    	});	
	    	
    };

}( jQuery ));