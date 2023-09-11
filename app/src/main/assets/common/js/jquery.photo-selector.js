(function( $ ) {

	var defaults = {
			hasUserMovedLeftOrRight: false
	};

    $.fn.photoSelector = function(options) {

    	return this.each(function() {
    	
	    	var self = this;
	    	var $this = $(this);
	    	var opts = $.extend( {}, defaults, options );
	
	    	var $wrapper = $('<div class="container-fluid photo-selector-wrapper"></div>').appendTo($this);
	    	var $row = $('<div class="row"></div>').appendTo($wrapper);
	    	$('<div class="col-12"><div class="open-selector-element photo"><span class="title">Upload photo</span></div></div>').appendTo($row);
    		var $next_row = $('<div class="row"></div>').appendTo($wrapper);
    		var $next_cell = $('<div class="col-12"></div>').appendTo($next_row);
    		
    		$row.find('.photo').on('click', function() {
    			
    			$next_cell.empty();
    	    	$next_cell.append('<input type="hidden" name="base64_string" value="" />');
    	    	$next_cell.append('<input type="file" style="display:none;" />');  // TODO: image-specific attribute
    	    	$next_cell.append('<div style="text-center msg"></div>');
    	    	$next_cell.append('<img src="" style="width:100%;" />');
    	    	$next_cell.find('input[type="file"]').on('change', function() {
    	    		 var reader = new FileReader();
    	    		 var f = this.files;
    	    		 $next_cell.find('.msg').text('Reading file from your device...');
    		    	 reader.onloadend = function () {
    		    		$next_cell.find('.msg').text('');
    		    		var encoded = reader.result;
    		    		var mime = encoded.substring("data:".length, encoded.indexOf(";base64"));
    		    		var mime_arr = mime.split('/');
    		    		if (mime_arr[0].toLowerCase() != 'image' || 'undefined' == typeof(mime_arr[1])) {
    		    			alert('Uploaded file was not an image. Please try again.');
    		    			return;
    		    		}
    		    		$next_cell.find('img').attr('src', encoded);
    		    		$next_cell.find('input[type="hidden"]').val(encoded);
    		    	 };
    		    	 setTimeout(function() {
    		    		reader.readAsDataURL(f[0]);
    	    		 }, 500);
    	    	}).trigger('click');
    	    	
    		});
	    	
	    	$this.find('form')[0].getValues = function() {
	    		var $form = $(this).closest('.container');
	    		if ($form.find('input[name="base64_string"]').length && $form.find('input[name="base64_string"]').val().length) {
	    			return ['photo', $form.find('input[name="base64_string"]').val()]
	    		} else {
	    			return [];
	    		}
	    	};
	    	
	    	$this.find('form')[0].setValues = function(values) {
	    		var $form = $(this);
	    		// TODO
	    	}
	    	
    	});	
	    	
    };

}( jQuery ));
