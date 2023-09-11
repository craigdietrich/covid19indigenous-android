(function( $ ) {

	var defaults = {
			hasUserMovedLeftOrRight: false
	};

    $.fn.audioSelector = function(options) {

    	return this.each(function() {
    	
	    	var self = this;
	    	var $this = $(this);
	    	var opts = $.extend( {}, defaults, options );
	
	    	var $wrapper = $('<div class="container-fluid open-selector-wrapper"></div>').appendTo($this);
	    	var $row = $('<div class="row"></div>').appendTo($wrapper);
	    	$('<div class="col-12"><div class="open-selector-element audio"><span class="title">Record audio</span></div></div>').appendTo($row);
    		var $next_row = $('<div class="row"></div>').appendTo($wrapper);
    		var $next_cell = $('<div class="col-12"></div>').appendTo($next_row);
    		
    		var calculateTimeDuration = function(secs) {
    		    var hr = Math.floor(secs / 3600);
    		    var min = Math.floor((secs - (hr * 3600)) / 60);
    		    var sec = Math.floor(secs - (hr * 3600) - (min * 60));

    		    if (sec < 10) {
    		        sec = "0" + sec;
    		    }

    		    if(hr <= 0) {
    		        return min + ':' + sec;
    		    }

    		    return hr + ':' + min + ':' + sec;
    		}
    		
	    	$row.find('.open-selector-element').on('click', function() {

	    				/*
	    				if (jQuery.browser.mobile) {
		    				$next_cell.append('<input type="hidden" name="base64_string" value="" />');
		    				$next_cell.append('<input type="file" style="display:none;" accept=".wav,audio/*" />');
		    				$next_cell.append('<div style="text-center msg"></div>');
		    				$next_cell.append('<audio class="open-audio" controls="" autoplay="" style="width:400px;display:none;"></video>');
		    				$next_cell.find('input[type="file"]').on('change', function() {
		    				 var reader = new FileReader();
		    				 var f = this.files;
		    				 $next_cell.find('.msg').text('Reading file from your device...');
			    			 reader.onloadend = function () {
			    				$next_cell.find('.msg').text('');
			    				var encoded = reader.result;
			    				var mime = encoded.substring("data:".length, encoded.indexOf(";base64"));
			    				var mime_arr = mime.split('/');
			    				if (mime_arr[0].toLowerCase() != 'audio' || 'undefined' == typeof(mime_arr[1])) {
			    					alert('Uploaded file was not audio. Please try again.');
			    					return;
			    				}
			    				$next_cell.find('audio').show().attr('src', encoded);
			    				$next_cell.find('input[type="hidden"]').val(encoded);
			    			 };
			    			 setTimeout(function() {
			    				reader.readAsDataURL(f[0]);
		    				 }, 500);
		    				}).trigger('click');
	    				} else {
	    				*/

	    		 			$next_cell.empty();
		    				$next_cell.append('<input type="hidden" name="base64_string" value="" />');
		    				$next_cell.append('<div style="margin-bottom:10px;"><button class="btn btn-success">Start recording</button><button class="btn btn-danger" style="display:none;">Stop recording</button></div>');
		    				$next_cell.append('<audio class="open-audio" controls="" style="width:100%;display:none;"></audio>');
		    	            $next_cell.append('<div class="progress" style="max-width:400px;margin:0px auto 0px auto;"><div class="progress-bar" style="width:0%;" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div></div>');
		    				$next_cell.append('<div class="progress-text"><span>0:00</span> / 1:00</div>')
		    	            navigator.mediaDevices.getUserMedia({ video: false, audio: true }).then(function(camera) {
		    	                var recordingHints = {
		    	                    type: 'audio',
		    	                   /* mimeType: 'audio/wav', */
		    	                    /* recorderType: StereoAudioRecorder */
		    	                };
		    	                recorder = RecordRTC(camera, recordingHints);  // Global
		    	                var is_recording = false;
		    	                $next_cell.find('button:first').on('click', function() {
		    	                	is_recording = true;
		    	                	var $this = $(this);
		    	                	$this.hide();
		    	                	$this.nextAll('button').show();
		    	                	recorder.startRecording();
		    	                	var timer = 0;
		    	                	var max_time = 60;
		    	                	var doTimer = function(timer) {
		    	                		if (!is_recording) return;
		    	                		if (timer == max_time) {
				    	                	is_recording = false;
				    	                	$(this).hide();
				    	                    recorder.stopRecording(function() {
				    	                        var blob = recorder.getBlob();
				    	                        $next_cell.find('audio')[0].muted = false;
				    	                        $next_cell.find('audio')[0].srcObject = null;
				    	                        camera.getTracks().forEach(function(track) {
				    	                            track.stop();
				    	                        });
				    	                        $next_cell.find('audio')[0].src = URL.createObjectURL(blob);
				    	                        $next_cell.find('audio').show();
				    	                        $next_cell.find('.progress, .progress-text').hide();
				    	                        var reader = new FileReader();
				    	                        reader.onloadend = function() {
				    	                            var encoded = reader.result;                
				    	                            $next_cell.find('input[type="hidden"]').val(encoded);
				    	                        }
				    	                        reader.readAsDataURL(blob); 
				    	                    });
				    	                    return;
		    	                		}
		    	                		timer++;
		    	                		if (timer == max_time) {
		    	                			$next_cell.find('button:last').trigger('click');
		    	                			return;
		    	                		}
		    	                		var str = calculateTimeDuration(timer);
		    	                		$this.closest('.row').find('.progress-text').find('span').text(str);
		    	                		var width = (timer / max_time) * 100;
		    	                		$this.closest('.row').find('.progress').children('div').css('width', width + '%');
		    	                		setTimeout(function() {
		    	                			doTimer(timer);
		    	                		}, 1000);
		    	                	}
		    	                	doTimer(timer);
		    	                });
		    	                $next_cell.find('button:last').on('click', function() {
		    	                	is_recording = false;
		    	                	$(this).hide();
		    	                    recorder.stopRecording(function() {
		    	                        var blob = recorder.getBlob();
		    	                        $next_cell.find('audio')[0].muted = false;
		    	                        $next_cell.find('audio')[0].srcObject = null;
		    	                        camera.getTracks().forEach(function(track) {
		    	                            track.stop();
		    	                        });
		    	                        $next_cell.find('audio')[0].src = URL.createObjectURL(blob);
		    	                        $next_cell.find('audio').show();
		    	                        $next_cell.find('.progress, .progress-text').hide();
		    	                        var reader = new FileReader();
		    	                        reader.onloadend = function() {
		    	                            var encoded = reader.result;                
		    	                            $next_cell.find('input[type="hidden"]').val(encoded);
		    	                        }
		    	                        reader.readAsDataURL(blob); 
		    	                    });
		    	                });
		    	            });
	    				// };
	    	});
	    	
	    	$this.find('form')[0].getValues = function() {
	    		var $form = $(this);
	    		var $container = $form.closest('.container');
	    		if ($container.find('input[name="base64_string"]').length && $container.find('input[name="base64_string"]').val().length) {
	    			return ['audio', $container.find('input[name="base64_string"]').val()]
	    		} else {
	    			return [];
	    		}
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
	    			case 'open-audio':
	    				// TODO
	    				break;
	    		}
	    	}
	    	
    	});	
	    	
    };

}( jQuery ));
