(function( $ ) {

	var defaults = {
		
	};

    $.fn.rangeSelector = function(options) {

    	String.prototype.ucword = function() {
    		var str = this.trim();
    		return str.charAt(0).toUpperCase() + str.slice(1);
    	};
    	
    	return this.each(function() {
    	
	    	var self = this;
	    	var $this = $(this);
	    	var opts = $.extend( {}, defaults, options );
	
	    	var doRangeColor = function() {
	    		var $range = $this.find('input[type="range"]:first');
				var value = $range.val();
				$range.removeClass('range-zero').removeClass('range-low').removeClass('range-med').removeClass('range-neu');
				if (value == 0) {
					$range.addClass('range-zero');
				} else if (value <= Math.ceil(opts.max * 0.33)) {
					$range.addClass('range-low');
				} else if (value < Math.ceil(opts.max * 0.66)) {
					$range.addClass('range-med');
				} else if (value == 6) {
					$range.addClass('range-neu');
				}
				if (value == 0) {
					$this.find('.selected_value').text('No answer');
				} else if (value == 1) {
					$this.find('.selected_value').text(opts.min_title);
				} else if (value == 2) {
					if ('undefined' != typeof(opts.midmin_title) && opts.midmin_title.length) {
						$this.find('.selected_value').text(opts.midmin_title);
					} else {
						$this.find('.selected_value').text(opts.min_title.toLowerCase().replace('great', '').replace('very', '').replace('strongly', '').ucword());
					}
				} else if (value == 3) {
					$this.find('.selected_value').text(opts.mid_title);
				} else if (value == 4) {
					if ('undefined' != typeof(opts.midmax_title) && opts.midmax_title.length) {
						$this.find('.selected_value').text(opts.midmax_title);
					} else {
						$this.find('.selected_value').text(opts.max_title.toLowerCase().replace('great', '').replace('very', '').replace('strongly', '').ucword());
					}
				} else if (value == 5) {
					$this.find('.selected_value').text(opts.max_title);
				} else if (value == 6) {
					$this.find('.selected_value').text("Don't know / Not applicable");
					$this.find('input[type="radio"]').prop('checked', true);
				}
				
	    	};
	    	
	    	var broadcastValueChange = function() {
	    		var value = $this.find('input[type="range"]:first').val();
	    		$this.trigger( "valueChange", [ value, this, opts ] );
	    	}
	    	
	    	doRangeColor();
	    	broadcastValueChange();
	    	$this.find('input[type="range"]').on('input', function() {
	    		var $range = $(this);
	    		$range.closest('.form-group').find('input[type="radio"]').prop('checked', false);
	    	});
	    	$this.find('input[type="radio"]').on('click', function() {
	    		var $radio = $(this);
	    		$radio.closest('.form-group').find('input[type="range"]').val(6);  // TODO: magic number 
	    		doRangeColor();
	    	});
	    	$this.find('input[type="range"]:first').on('input', doRangeColor); 
	    	$this.find('input[type="range"]:first').on('input', broadcastValueChange);
	    	
	    	$this.find('form')[0].getValues = function() {
	    		var $form = $(this);
	    		return [$form.find('input').val()];
	    	};
	    	
	    	$this.find('form')[0].setValues = function(values) {
	    		var $form = $(this);
	    		$form.find('input').val(values[0]);
	    		$form.find('input[type="range"]:first').trigger('input');
	    	}
	    	
    	});	
	    	
    };

}( jQuery ));