(function( $ ) {

	var defaults = {
		dataSource: '',
		id: null,
		key: null
	};

    $.fn.validate = function(options, callback) {

    	if ('function' == typeof(options)) {
    		callback = options;
    		options = null;
    	};
    	
    	if ('undefined' == callback) callback = null;
    	
    	return this.each(function() {
    	
	    	var self = this;
	    	var $this = $(this);
	    	var opts = $.extend( {}, defaults, options );
	
	    	if (!opts.dataSource || !opts.dataSource.length) {
	    		alert('dataSource is a required field');
	    		return;
	    	}
	    	
	    	if ($('.modal.show').length) {
	    		$('.modal.show').removeClass('fade').modal('hide');
	    	}
	    	
	    	$this.addClass('fade');
	    	$('article:first').empty().append('<div class="spinner-wrapper"><div class="spinner-border text-primary" role="status"><span class="sr-only">Loading...</span></div></div>');
	    	if (opts.key) $this.find('[name="code"]').val(opts.key);
	    	$this.find('#submitCodeForm').off('submit').on('submit', function() {
	    		$this.removeClass('fade');
	    		var code = $(this).find('[name="code"]').val();
	    		if (!code.length) return false;
	    		var hash = '#';
	    		if (opts.id) hash += 'id='+opts.id+'&';
	    		hash += 'key='+code;
	    		window.location.hash = hash;
	    		return false;
	    	});
	    	$this.find('#re-enter-code').off('click').on('click', function() {
	    		var hash = '#';
	    		if (opts.id) hash += 'id='+opts.id;
	    		window.location.hash = hash;
	    		return false;
	    	});
	    	
	    	if (opts.id && opts.key) {  // Ready to take questionnaire

	        	$.ajax({
		    	    url: opts.dataSource + opts.id + '?key=' + opts.key,
		    	    jsonp: "callback",
		    	    dataType: "jsonp",
		    	    data: {},
		    	    success: function( response ) {

		    	    	$('.spinner-wrapper').remove();
		    			$this.find('.no_questionnaires').hide();
		    			$this.find('.has_questionnaires').show();
		    			
			    		if (!response.length) {
			    			hash = '#key=' + opts.key;
			    			window.location.hash = hash;
			    			return;
			    		}
			    		
			    		for (var j = 0; j < response.length; j++) {
			    			if (parseInt(response[j].questionnaire_id) == opts.id) {
			    				if (callback) callback();
			    				return;
			    			}
			    		}
			    		
			    		hash = '#key=' + opts.key;
			    		window.location.hash = hash;
		    	        
		    	    },
		    	    error: function( error ) {
		    	    	alert('There was a problem trying to get a list of questionnaires');
		    	    	console.log(error);
		    	    }
		    	});	   		
	    		
	    	} else if (opts.id) {  // Take a public questionnaire
	    		
	        	$.ajax({
		    	    url: opts.dataSource,
		    	    jsonp: "callback",
		    	    dataType: "jsonp",
		    	    data: {},
		    	    success: function( response ) {
		    	    	
		    	    	$('.spinner-wrapper').remove();
		    			$this.find('.no_questionnaires').hide();
		    			$this.find('.has_questionnaires').show();
		    			
			    		if (!response.length) {
			    			hash = '#';
			    			window.location.hash = hash;
			    			return;
			    		}

			    		for (var j = 0; j < response.length; j++) {
			    			if (parseInt(response[j].questionnaire_id) == opts.id) {
			    				if (callback) callback();
			    				return;
			    			}
			    		}
			    		
			    		hash = '#';
			    		window.location.hash = hash;
		    	        
		    	    },
		    	    error: function( error ) {
		    	    	alert('There was a problem trying to get a list of questionnaires');
		    	    	console.log(error);
		    	    }
		    	});	
	    		
	    	} else if (opts.key) {  // Get list of questionnaires per user or community

	        	$.ajax({
		    	    url: opts.dataSource + '?key=' + opts.key,
		    	    jsonp: "callback",
		    	    dataType: "jsonp",
		    	    data: {},
		    	    success: function( response ) {

		    	    	$('.spinner-wrapper').remove();
		    			$this.find('.no_questionnaires').hide();
		    			$this.find('.has_questionnaires').show();
		    	    	
			    		$this.find('.header-text').html('Click a survey below to take it right now');
			    		var $tbody = $this.find('tbody').empty();
			    		
			    		if (!response.length) {
			    			$tbody.append('<div class="text-center">There are no questionnaires presently available. Try a different code?</div>');
			    		}
			    		
			    		for (var j = 0; j < response.length; j++) {
			    			var $row = $('<tr data-questionnaire-id="'+response[j].questionnaire_id+'"></tr>').appendTo($tbody);
			    			title = response[j].title;
			    			if (response[j].subtitle.length) title += ': ' + response[j].subtitle;
			    			$('<td class="title text-primary">'+title+'</td>').appendTo($row);
			    		}
			    		
			    		$tbody.find('tr').on('click', function() {
			    			var questionnaire_id = parseInt( $(this).data('questionnaire-id') );
			    			var hash = '#id=' + questionnaire_id + '&key=' + opts.key;
			    			window.location.hash = hash;
			    			$this.modal('hide');
			    		});
			    		
			    		$this.modal();
		    	        
		    	    },
		    	    error: function( error ) {
		    	    	alert('There was a problem trying to get a list of public questionnaires');
		    	    	console.log(error);
		    	    }
		    	});	
	    		
	    	} else {
	
	        	$.ajax({
		    	    url: opts.dataSource,
		    	    jsonp: "callback",
		    	    dataType: "jsonp",
		    	    data: {},
		    	    success: function( response ) {
			 
		    	    	$('.spinner-wrapper').remove();
		    	    	
			    		$this.find('.header-text').html('Here are public surveys that you can take right now');
			    		var $tbody = $this.find('tbody').empty();

			    		if (!response.length) {
			    			$this.find('.no_questionnaires').show();
			    			$this.find('.has_questionnaires').hide();
			    		}
			    		
			    		for (var j = 0; j < response.length; j++) {
			    			var $row = $('<tr data-questionnaire-id="'+response[j].questionnaire_id+'"></tr>').appendTo($tbody);
			    			title = response[j].title;
			    			if (response[j].subtitle.length) title += ': ' + response[j].subtitle;
			    			$('<td class="title text-primary">'+title+'</td>').appendTo($row);
			    			$('<td class="date text-primary">'+response[j].date_human+'</td>').appendTo($row);
			    		}
			    		
			    		$tbody.find('tr').on('click', function() {
			    			var questionnaire_id = parseInt( $(this).data('questionnaire-id') );
			    			var hash = '#id=' + questionnaire_id;
			    			window.location.hash = hash;
			    			$this.modal('hide');
			    		});
			    		
			    		$this.modal();
		    	        
		    	    },
		    	    error: function( error ) {
		    	    	alert('There was a problem trying to get a list of public questionnaires');
		    	    	console.log(error);
		    	    }
		    	});	
	    		
	    	}
	    	
    	});	
	    	
    };

}( jQuery ));