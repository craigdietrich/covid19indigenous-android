(function( $ ) {

	var defaults = {
			hasUserMovedLeftOrRight: false
	};

    $.fn.openSelector = function(options) {

    	return this.each(function() {
    	
	    	var self = this;
	    	var $this = $(this);
	    	var opts = $.extend( {}, defaults, options );
	
	    	var $wrapper = $('<div class="container-fluid open-selector-wrapper"></div>').appendTo($this);
	    	var $row = $('<div class="row"></div>').appendTo($wrapper);
	    	$('<div class="col-6 col-md-4"><div class="open-selector-element text"><span class="title">Write text</span></div></div>').appendTo($row);
	    	$('<div class="col-6 col-md-4"><div class="open-selector-element video"><span class="title">Record video</span></div></div>').appendTo($row);
	    	$('<div class="col-6 col-md-4"><div class="open-selector-element photo"><span class="title">Upload photo</span></div></div>').appendTo($row);
	    	//$('<div class="col-6 col-md-3 col-lg-3 col-xl-3"><div class="open-selector-element audio"><span class="title">Record audio</span></div></div>').appendTo($row);
	    	//$('<div class="col-6 col-md-4 col-lg-2 col-xl-2"><div class="open-selector-element draw"><span class="title">Make drawing</span></div></div>').appendTo($row);
	    	//$('<div class="col-6 col-md-4 col-lg-2 col-xl-2"><div class="open-selector-element song"><span class="title">Record song</span></div></div>').appendTo($row);

	    	$row.find('.open-selector-element').on('click', function() {
	    		$el = $(this);
	    		// Check to see if there is alread an element selected
	    		var $selectedItem = $el.closest('.row').find('.selected');
	    		if ($selectedItem.length && !$('body').hasClass('modal-open')) {
	    			var $modal = $('#confirmSwitchMediaTypeModal');
	    			$modal.data('$element', $el);
	    			$modal.modal();
	    			$modal.find('button:first').off('click').on('click', function() {
	    				$modal.modal('hide');
	    			});
	    			$modal.find('button:last').off('click').on('click', function() {
	    				var $el = $('#confirmSwitchMediaTypeModal').data('$element');
	    				$el.click();
	    				$modal.modal('hide');
	    			});
	    			return;
	    		}
	    		// Set the item that is selected
	    		var type = $el.attr('class').replace('open-selector-element ', '').replace(' selected', '');
	    		$row = $el.closest('.row');
	    		$row.next().find('.open-video').each(function() {
	    			console.log(this);
	    			this.srcObject = null;
	    		})
	    		$row.next().remove();
	    		$el.parent().parent().find('.selected').removeClass('selected');
	    		$el.addClass('selected');
	    		// Add the HTML area for the selected item
	    		var $next_row = $('<div class="row"></div>').appendTo($wrapper);
	    		var $next_cell = $('<div class="col-12"></div>').appendTo($next_row);
	    		var is_safari = /^((?!chrome|android).)*safari/i.test(navigator.userAgent);
	    		switch (type) {
	    			case 'text':
	    				$next_cell.append('<textarea class="form-control"></textarea>');
	    				$next_cell.find('textarea').focus();
	    				break;
	    			case 'photo':
	    				$next_cell.append('<input type="hidden" name="base64_string" value="" />');
	    				$next_cell.append('<input type="file" style="display:none;" />');  // TODO: image-specific attribute
	    				$next_cell.append('<div style="text-center msg"></div>');
	    				$next_cell.append('<img src="" style="width:400px;" />');
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
	    				break;
	    			case 'video':
	    				if (jQuery.browser.mobile || is_safari) {
		    				$next_cell.append('<input type="hidden" name="base64_string" value="" />');
		    				$next_cell.append('<input type="file" style="display:none;" />');  // TODO: video-specific attribute
		    				$next_cell.append('<div style="text-center msg"></div>');
		    				$next_cell.append('<video class="open-video" controls="" autoplay="" style="width:400px;height:250px;"></video>');
		    				$next_cell.find('input[type="file"]').on('change', function() {
		    					 var reader = new FileReader();
		    					 var f = this.files;
		    					 $next_cell.find('.msg').text('Reading file from your device...');
			    				 reader.onloadend = function () {
			    					$next_cell.find('.msg').text('');
			    					var encoded = reader.result;
			    					var mime = encoded.substring("data:".length, encoded.indexOf(";base64"));
			    					var mime_arr = mime.split('/');
			    					if (mime_arr[0].toLowerCase() != 'video' || 'undefined' == typeof(mime_arr[1])) {
			    						alert('Uploaded file was not a video. Please try again.');
			    						return;
			    					}
			    					$next_cell.find('video').attr('src', encoded);
			    					$next_cell.find('input[type="hidden"]').val(encoded);
			    				 };
			    				 setTimeout(function() {
			    					reader.readAsDataURL(f[0]);
		    					 }, 500);
		    				}).trigger('click');	    					
	    				} else {
		    				$next_cell.append('<input type="hidden" name="base64_string" value="" />');
		    				$next_cell.append('<div style="margin-bottom:10px;"><button class="btn btn-success">Start recording</button><button class="btn btn-danger" style="display:none;">Stop recording</button></div>');
		    				$next_cell.append('<video class="open-video" controls="" autoplay="" style="width:400px;height:250px;"></video>');
		    	            navigator.mediaDevices.getUserMedia({ video: true, audio: true }).then(function(camera) {
		    	                $next_cell.find('video')[0].muted = true;
		    	                $next_cell.find('video')[0].srcObject = camera;
		    	                var recordingHints = {
		    	                    type: 'video'
		    	                };
		    	                recorder = RecordRTC(camera, recordingHints);  // Global
		    	                $next_cell.find('button:first').on('click', function() {
		    	                	$(this).hide();
		    	                	$(this).nextAll('button').show();
		    	                	recorder.startRecording();
		    	                });
		    	                $next_cell.find('button:last').on('click', function() {
		    	                	$(this).hide();
		    	                    recorder.stopRecording(function() {
		    	                        var blob = recorder.getBlob();
		    	                        $next_cell.find('video')[0].muted = false;
		    	                        $next_cell.find('video')[0].srcObject = null;
		    	                        camera.getTracks().forEach(function(track) {
		    	                            track.stop();
		    	                        });
		    	                        $next_cell.find('video')[0].src = URL.createObjectURL(blob);
		    	                        var reader = new FileReader();
		    	                        reader.onloadend = function() {
		    	                            var encoded = reader.result;                
		    	                            $next_cell.find('input[type="hidden"]').val(encoded);
		    	                        }
		    	                        reader.readAsDataURL(blob); 
		    	                    });
		    	                });
		    	            });
	    				};
	    	            break;
	    		}
	    		// Broadcast
		    	$this.trigger( "valueChange", [ type, this, opts ] );
	    	});
	    	
	    	$this.find('form')[0].getValues = function() {
	    		var $form = $(this);
	    		var $container = $form.closest('.container');
	    		var $selected = $container.find('.selected');
	    		if (!$selected.length) return [];
	    		if ($selected.hasClass('text')) {
	    			return ['open-text', $container.find('textarea').val()]
	    		} else if ($selected.hasClass('photo')) {
	    			return ['open-photo', $container.find('input[name="base64_string"]').val()]
	    		} else if ($selected.hasClass('video')) {
	    			return ['open-video', $container.find('input[name="base64_string"]').val()]
	    		}
	    		return [];
	    	};
	    	
	    	$this.find('form')[0].setValues = function(values) {
	    		var $form = $(this);
	    		var $container = $form.closest('.container');
	    		if ('undefined' == typeof(values[0]) || 'undefined' == typeof(values[1])) return;
	    		var type = values[0];
	    		switch (type) {
	    			case 'open-text':
	    				$container.find('.text').click();
	    				$container.find('textarea').val(values[1]);
	    				break;
	    			case 'open-photo':
	    				// TODO
	    				break;
	    			case 'open-video':
	    				// TODO
	    				break;
	    		}
	    	}
	    	
    	});	
	    	
    };

}( jQuery ));