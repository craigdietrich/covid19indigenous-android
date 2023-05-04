(function( $ ) {

	var defaults = {
		
	};

    $.fn.rankedSelector = function(options) {

    	return this.each(function() {
    	
	    	var self = this;
	    	var $this = $(this);
	    	var opts = $.extend( {}, defaults, options );
	
	    	var broadcastValueChange = function() {
	    		var value = $(this).data('index');
	    		if ('undefined' == typeof(value)) return;
	    		$this.trigger( "valueChange", [ value, this, opts ] );
	    		$this.find('[name="no-answer"]').prop('checked', false);
	    	}
	    	
	    	$this.find('li').on('click', broadcastValueChange);
	    	
	    	$this.find('.ranked-choice').sortable();
	    	
	    	$this.find('form')[0].getValues = function() {
	    		var $form = $(this);
	    		var values = [];
	    		if ($this.find('[name="no-answer"]').is(':checked')) {  // Choices haven't been interacted with
	    			return values;
	    		}
	    		$form.find('li').each(function() {
	    			values.push($(this).text());
	    		});
	    		return values;
	    	};
	    	
	    	$this.find('form')[0].setValues = function(values) {
	    		
	    		var $form = $(this);
	    		var $ul = $form.find('ul');
	    		
		    	var sort_li = function (a, b){
		    	    var a_text = $(a).text();
		    	    var b_text = $(b).text();
		    	    var a_index = values.indexOf(a_text);
		    	    var b_index = values.indexOf(b_text);
		    	    return (a_index < b_index) ? -1 : 1;
		    	}
	    		
	    		$form.find('li').sort(sort_li).appendTo($ul);
	    		$form.find('li:first').click();
	    	}

	    	
    	});	
	    	
    };

}( jQuery ));