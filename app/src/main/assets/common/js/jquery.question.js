(function( $ ) {

	var defaults = {
		
	};

    $.fn.question = function(options) {

    	var doWrapper = function(data, insertHtml) {
    		
    		var html = '';
    		if ('undefined' != typeof(data.question_number)) {
	    		html += '  <div class="row">';
	    		html += '    <div class="col">';
	    		html += '      <div class="question-marker">Question '+data.question_number+'</div>';
	    		html += '    </div>';
	    		html += '  </div>';
    		}
    		html += '  <div class="row">';
    		html += '    <div class="col">';
    		html += '      <form data-question-id="'+data.question_id+'" onsubmit="return false;">';
   			if ('undefined' != typeof(data.title)) html += '      <p class="title">'+data.title+'</p>';
   			html += insertHtml;
   			html += '      </form>';
   			html += '      <div class="sub_questions"></div>';
   			html += '    </div>';
   			html += '  </div>';
   			return html;
    		
    	}
    	
    	var doSlider = function(data) {
    		
    		if ('undefined' == typeof(window['slider_count'])) window['slider_count'] = 0;
    		window['slider_count']++;
    		
    		var html = '';
    		html += '<p class="description more-margin">To move the slider below, click the circle and drag or click anywhere on the bar</p>';
    		html += '<div class="form-group">';
    		html += '<div class="row">';
    		html += '<div class="col-12 col-md-10">';
    		html += '  <input type="range" ';
    		html += 'min="0" max="6" value="0" ';
    		html += '/>';
    		/*
    		if ('undefined' != typeof(data.min_title) && 'undefined' != typeof(data.max_title)) {
    			html += '<div class="float-right" style="padding-top:10px;">'+data.max_title+'</div>';
    			html += '<div class="float-left" style="padding-top:10px;">'+data.min_title+'</div>';
    		}
    		*/
    		html += '<div class="selected_value" style="font-weight:bold;padding-top:10px;">No answer</div>';
    		html += '</div>';
    		html += '<div class="col-12 col-md-2 pt-4 pt-md-0">';
    		html += "<label style=\"font-weight:bold;\">Don't know / Not applicable<br />"+'<input type="radio" name="dk-na-'+window['slider_count']+'" value="dk" /></label>';
    		html += '</div>';
    		html += '</div>';
    		html += '</div>';
    		return doWrapper(data, html);
    		
    	}
    	
    	var doLikert = function(data) {

    		if ('undefined' == typeof(window['likert_count'])) window['likert_count'] = 0;
    		window['likert_count']++;
    		
    		var html = '';
    		html += '<table class="table table-striped">';
    		html += '<thead>';
    		html += '<tr>';
    		html += '<th scope="col">&nbsp;</th>';
    		if ('string' == typeof(data.prompts)) data.prompts = data.prompts.split(';');
    		for (var j = 0; j < data.prompts.length; j++) {
    			html += '<th scope="col">'+data.prompts[j]+'</th>';
    		};
    		html += '</tr>';
    		html += '</thead>';
    		html += '<tbody>';
    		if ('string' == typeof(data.answers)) data.answers = data.answers.split(';');
    	   	for (var j = 0; j < data.answers.length; j++) {
    	   		html += '<tr>';
        		html += '<td class="text-left">'+data.answers[j]+'</td>';
        		for (var k = 0; k < data.prompts.length; k++) {
        			html += '<td><input type="radio" name="radio_'+window['likert_count']+'_'+j+'" value="'+(k+1)+'" /></td>';
        		}
        		html += '</tr>';
        	};
        	html += '</tbody>';
        	html += '</table>';
    		return doWrapper(data, html);
    		
    	}
    	
       	var doLikertSlider = function(data) {

    		if ('undefined' == typeof(window['likert_count'])) window['likert_count'] = 0;
    		window['likert_count']++;
    		
    		var html = '';
    		html += '<p class="description more-margin">To move the sliders below, click the circles and drag or click anywhere on the bars</p>';
    		html += '<table class="likert-slider-table">';
    		html += '<tbody>';
    		if ('string' == typeof(data.answers)) data.answers = data.answers.split(';');
    		if ('string' == typeof(data.prompts)) data.prompts = data.prompts.split(';');
    		var prompts = []
    		for (var j = 0; j < data.prompts.length; j++) {
    			if (data.prompts[j].trim().toLowerCase() == "don't know") continue;
    			if (data.prompts[j].trim().toLowerCase() == "not applicable") continue;
    			prompts.push( data.prompts[j] );
    		}
    	   	for (var j = 0; j < data.answers.length; j++) {
    	   		html += '<tr>';
        		html += '<td class="text-center">';
        		html += '<div class="row">';
        		html += '<div class="col-12 col-md-10">';
        		html += '<span class="answer">'+data.answers[j]+'</span><br />';
        		html += '<input type="range" class="range_low" value="0" min="0" max="'+(prompts.length + 1)+'" name="range_'+window['likert_count']+'_'+j+'" data-prompts="'+prompts.join(';')+'" />';
        		html += '<br /><b></b>';
        		html += '</div>';
        		html += '<div class="col-12 col-md-2 mt-4 mt-md-0">';
        		html += "<label>Don't know / Not applicable<br />"+'<input type="radio" name="dk-na-'+window['likert_count']+'-'+j+'" value="dk" /></label>';
        		html += '</div>';
        		html += '</div>';
        		html += '</td>';
        		html += '</tr>';
        	};
        	html += '</tbody>';
        	html += '</table>';
    		return doWrapper(data, html);
    		
    	}
    	
    	var doRanked = function(data) {
    		
    		var html = '';
    		html += '<p class="description">Drag and drop items to rank with the most <i>important item</i> at the <i>top</i> and the <i>least important</i> item at the <i>bottom</i></p>';
    		html += '<p><label><input type="checkbox" name="no-answer" checked /> &nbsp; <b>No answer</b></label></p>';
    		html += '<p><b>Most important</b></p>';
    		html += '<ul class="list-group ranked-choice">';
    		if ('string' == typeof(data.answers)) data.answers = data.answers.split(';');
    		for (var j = 0; j < data.answers.length; j++) {
    			html += '<li class="list-group-item" data-index="'+j+'">'+data.answers[j]+'</li>';
    		}
    		html += '</ul>';
    		html += '<p class="mt-3"><b>Least important</b></p>';
    		return doWrapper(data, html);
    		
    	}
    	
    	var doText = function(data) {
    		
    		var html = '';
    		html += '<div class="form-group">';
    		html += '  <textarea class="form-control"></textarea>';
    		html += '</div>';
    		return doWrapper(data, html);
    		
    	}
    	
    	var doSentence = function(data) {
    		
    		var html = '';
    		html += '<div class="form-group">';
    		html += '  <input type="text" class="form-control" />';
    		html += '</div>';
    		return doWrapper(data, html);
    		
    	}
    	
    	var doNumber = function(data) {
    		
    		var html = '';
    		html += '<div class="form-group">';
    		html += '  <input type="number" step="0.01" class="form-control" placeholder="Please enter a number" />';
    		html += '</div>';
    		return doWrapper(data, html);
    		
    	}
    	
    	var doLatlng = function(data) {
    		
    		var html = '';
			html += '<div class="row">';
	    	html += '  <div class="col"><input type="text" class="form-control" name="latitude" placeholder="Latitude" /></div><div class="col"><input type="text" class="form-control" name="longitude" placeholder="Longitude" /></div>';
			html += '</div>';
			html += '<div class="row">';
	    	html += '  <div class="col text-center"><div class="latlngError mb-4 mt-4 text-danger"></div><button type="button" class="btn btn-secondary" name="locationButton">Click to set exact location</button></div>';
			html += '</div>';
    		return doWrapper(data, html);
    		
    	}
    	
    	var doIntermissionUserFields = function(data) {
    		
    		var html = '';
			html += '<div class="container-fluid intermission-border user-fields">';
			html += '  <div class="row">';
			html += '     <div class="col-12 intermission-wrapper">';
	    	html += '		<form class="container-fluid">';
	    	html += '  		  <div class="row">';
	    	html += '    	    <div class="col-12">Filling in this information helps us compile better survey results. Please read our <a href="privacy-policy.html" target="_blank">privacy policy</a>.</div>';
	    	html += '  	     </div>';
	    	html += '        <div class="row">';
	    	html += '          <div class="col-12 col-sm-6 col-lg-3"><input type="text" class="form-control" name="location" placeholder="Location (optional)" /><button type="button" class="btn text-primary" name="locationButton">Click to set exact location</button></div>';
	    	html += '          <div class="col-12 col-sm-6 col-lg-3"><select class="form-control" name="gender"><option value="">Gender</option><option value="female">Female</option><option value="male">Male</option><option value="other">Other</option></select></div>';
	    	html += '          <div class="col-12 col-sm-6 col-lg-3"><select class="form-control" name="age"><option value="">Age</option>';
    		html += '<option value="0-12">0-12</option><option value="13-19">13-19</option><option value="20-29">20-29</option><option value="30-39">30-39</option><option value="40-49">40-49</option><option value="50-49">50-49</option>';
    		html += '<option value="60-69">60-69</option><option value="70-79">70-79</option><option value="80-89">80-89</option><option value="90+">90+</option>';
	    	html += '</select></div>';
	    	html += '          <div class="col-12 col-sm-6 col-lg-3"><input type="text" class="form-control" name="name" placeholder="Your name (optional)" /></div>';
	    	html += '        </div>';
	    	html += '       </form>';
			html += '     </div>';
			html += '  </div>';
			html += '</div>';
    		return doWrapper(data, html);
    		
    	}
    	
    	var doIntermissionText = function(data) {
    		
    		var html = '';
			html += '<div class="container-fluid intermission-border text">';
			html += '  <div class="row">';
			html += '     <div class="col-12 intermission-wrapper">';
			html += data.text;
			html += '     </div>';
			html += '  </div>';
			html += '</div>';
    		return doWrapper(data, html);
    		
    	}
    	
    	var doCheck = function(data) {
    		
    		var html = '';
    		html += '<div class="form-group">';
    		html += '<div class="container-fluid">';
    		html += '<div class="row">';
    		if ('undefined' != typeof(data.answers)) {
    			if ('string' == typeof(data.answers)) data.answers = data.answers.split(';');
    			var col_num = 4;
    			if (data.answers.length < 3) col_num = 6;
    			for (var j = 0; j < data.answers.length; j++) {	
    				html += '<div class="col-6 col-md-'+col_num+' form-check-wrapper">';
    				html += '<div class="form-check">';
    				html += '<label class="form-check-label">';
    				html += '<input class="form-check-input" type="checkbox" value="'+j+'">';
    				html += data.answers[j]+'</label>';
    				html += '</div>';
    				html += '</div>';
    			}
    		}
    		html += '</div>';
    		if ('undefined' != typeof(data.include_other) && parseInt(data.include_other)) {
	    		html += '<div class="row">';
	    		html += '  <div class="col-12 form-check-wrapper">';
	    		html += '    <div class="form-group">';
	    		html += '      <label>Other</label>';
	    		html += '      <input type="text" name="other" class="form-control" />';
	    		html += '    </div>';    		
	    		html += '  </div>';
	    		html += '</div>'	
    		}
    		html += '</div>';
    		html += '</div>';  
    		return doWrapper(data, html);
    		
    	}
    	
    	var doRadio = function(data) {
    		
    		if ('undefined' == typeof(window['radio_count'])) window['radio_count'] = 0;
    		window['radio_count']++;
    		var html = '';
    		html += '<div class="form-group">';
    		html += '<div class="container-fluid">';
    		html += '<div class="row">';
    		if ('undefined' != typeof(data.answers)) {
    			if ('string' == typeof(data.answers)) data.answers = data.answers.split(';');
    			var col_num = 4;
    			if (data.answers.length < 3) col_num = 6;
    			for (var j = 0; j < data.answers.length; j++) {	
    				html += '<div class="col-6 col-md-'+col_num+' form-check-wrapper">';
    				html += '<div class="form-check">';
    				html += '<label class="form-check-label">';
    				html += '<input class="form-check-input" type="radio" name="radio_'+window['radio_count']+'" value="'+j+'">';
    				html += data.answers[j]+'</label>';
    				html += '</div>';
    				html += '</div>';
    			}
    		}
    		html += '</div>';
    		if ('undefined' != typeof(data.include_other) && parseInt(data.include_other)) {
	    		html += '<div class="row">';
	    		html += '  <div class="col-12 form-check-wrapper">';
	    		html += '    <div class="form-group">';
	    		html += '      <label>Other</label>';
	    		html += '      <input type="text" name="other" class="form-control" />';
	    		html += '    </div>';    		
	    		html += '  </div>';
	    		html += '</div>'	
    		}
    		html += '</div>';
    		html += '</div>';  		
    		return doWrapper(data, html);
    		
    	}
    	
    	var doConsent = function(data) {

	    	var nl2br = function (str, isXhtml) {
	    		// https://locutus.io/php/nl2br/
	    		if (typeof str === 'undefined' || str === null) {
	    			return ''
	    		}
	    		const breakTag = (isXhtml || typeof isXhtml === 'undefined') ? '<br ' + '/>' : '<br>'
	    		return (str + '').replace(/(\r\n|\n\r|\r|\n)/g, breakTag + '$1')
	    	}
    		
    		if ('undefined' == typeof(window['radio_count'])) window['radio_count'] = 0;
    		window['radio_count']++;
    		var html = '';
    		html += '<div class="form-group">';
    		html += '<div class="container-fluid">';
    		
    		html += '<div class="row text-left mb-4"><p>';
    		html += nl2br(data.text, true);
    		html += '</p></div>';
    		
    		html += '<div class="row">';
    		
    		html += '<div class="col-6 form-check-wrapper">';
    		html += '<div class="form-check">';
    		html += '<label class="form-check-label">';
    		html += '<input class="form-check-input" type="radio" name="radio_'+window['radio_count']+'" value="0">';
    		html += data.no_title+'</label>';
    		html += '</div>';
    		html += '</div>';
    		
    		html += '<div class="col-6 form-check-wrapper">';
    		html += '<div class="form-check">';
    		html += '<label class="form-check-label">';
    		html += '<input class="form-check-input" type="radio" name="radio_'+window['radio_count']+'" value="1">';
    		html += data.yes_title+'</label>';
    		html += '</div>';
    		html += '</div>';
    		
    		html += '</div>';
    		html += '</div>';
    		html += '</div>';  		
    		return doWrapper(data, html);
    		
    	}
    	
    	var doNoYes = function(data) {
    		
    		if ('undefined' == typeof(window['radio_count'])) window['radio_count'] = 0;
    		window['radio_count']++;
    		var html = '';
    		html += '<div class="form-group">';
    		html += '<div class="container-fluid">';
    		
    		html += '<div class="row">';
    		
    		html += '<div class="col-6 form-check-wrapper">';
    		html += '<div class="form-check">';
    		html += '<label class="form-check-label">';
    		html += '<input class="form-check-input" type="radio" name="radio_'+window['radio_count']+'" value="0">';
    		html += data.no_title+'</label>';
    		html += '</div>';
    		html += '</div>';
    		
    		html += '<div class="col-6 form-check-wrapper">';
    		html += '<div class="form-check">';
    		html += '<label class="form-check-label">';
    		html += '<input class="form-check-input" type="radio" name="radio_'+window['radio_count']+'" value="1">';
    		html += data.yes_title+'</label>';
    		html += '</div>';
    		html += '</div>';
    		
    		html += '</div>';
    		html += '</div>';
    		html += '</div>';  		
    		return doWrapper(data, html);
    		
    	}
    	
    	var doOpenEnded = function(data) {
    		
    		var html = '';
    		html += '<div class="open-selector"></div>';
    		return doWrapper(data, html);
    		
    	}
    	
    	var doPhoto = function(data) {
    		
    		var html = '';
    		html += '<div class="photo-selector"></div>';
    		return doWrapper(data, html);
    		
    	}
    	
    	var doAudio = function(data) {
    		
    		var html = '';
    		html += '<div class="audio-selector"></div>';
    		return doWrapper(data, html);
    		
    	}
    	
    	return this.each(function() {
    	
	    	var self = this;
	    	var $this = $(this);
	    	var opts = $.extend( {}, defaults, options );

	    	var $el = $('<div class="container question"></div>').appendTo($this);
	    	$el.data('opts', opts);

	    	// Capture value changes to possibly present child questions
	    	$el.on('valueChange', function(event, _value, _element, _opts) {
	    		$(_element).closest('form').nextAll('.sub_questions:first').children('section').each(function() {
	    			var $section = $(this);
	    			var values_arr = $section.data('values').toString().split(',');
	    			if ('any' == values_arr[0] || 'all' == values_arr[0]) {
	    				$section.show();
	    				return;
	    			} 
	    			for (var j = 0; j < values_arr.length; j++) {
	    				values_arr[j] = parseInt(values_arr[j]);
	    			}
	    			if (-1 != values_arr.indexOf(parseInt(_value))) {
	    				$section.show();
	    			} else {
	    				$section.hide();
	    			}
	    		});
	    	});
	    	
	    	// Create the question and run any special jQuery object on it
	    	var type = ('undefined' != typeof(opts.type)) ? opts.type : null;
	    	switch(type) {
	    		case "slider":
	    			$el.append( doSlider(opts) ).rangeSelector(opts);
	    			break;
	    		case "consent":
	    			$el.append( doConsent(opts) ).consentSelector(opts);
	    			break;
	    		case "noyes":
	    			$el.append( doNoYes(opts) ).noyesSelector(opts);
	    			break;
	    		case "text":
	    			$el.append( doText(opts) ).textSelector(opts);
	    			break;
	    		case "sentence":
	    			$el.append( doSentence(opts) ).sentenceSelector(opts);
	    			break;
	    		case "number":
	    			$el.append( doNumber(opts) ).numberSelector(opts);
	    			break;
	    		case "latlng":
	    			$el.append( doLatlng(opts) ).latlngSelector(opts);
	    			break;	    			
	    		case "check":
	    			$el.append( doCheck(opts) ).checkSelector(opts);
	    			break;
	    		case "radio":
	    			$el.append( doRadio(opts) ).radioSelector(opts);
	    			break;
	    		case "likert":
	    			$el.append( doLikert(opts) ).likertSelector(opts);
	    			break;
	    		case "likert-slider":
	    			$el.append( doLikertSlider(opts) ).likertSliderSelector(opts);
	    			break;
	    		case "ranked":
	    			$el.append( doRanked(opts) ).rankedSelector(opts);
	    			break;
	    		case "photo":
	    			$el.append( doPhoto(opts) ).photoSelector(opts);
	    			break;
	    		case "audio":
	    			$el.append( doAudio(opts) ).audioSelector(opts);
	    			break;
	    		case 'intermission-userfields':
	    			$el.append( doIntermissionUserFields(opts) ).intermission(opts);
	    			break;	    			
	    		case 'intermission-text':
	    			$el.append( doIntermissionText(opts) ).intermission(opts);
	    			break;
	    		default:
	    			$el.append( doOpenEnded(opts) ).openSelector(opts);
	    	};

	    	// Create sub questions
	    	var $sub_questions = $el.find('.sub_questions:first');
    		if ('undefined' != typeof(opts.questions)) {
	    		for (var j = 0; j < opts.questions.length; j++) {
	    			var $section = $('<section data-values="'+opts.questions[j].parent_values+'"></section>').question(opts.questions[j]).appendTo($sub_questions);
	    			if ('any' != opts.questions[j].parent_values && 'all' != opts.questions[j].parent_values) $section.hide();
	    		};
    		};
	    	
    	});	
	    	
    };

}( jQuery ));