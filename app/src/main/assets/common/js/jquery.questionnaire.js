(function( $ ) {

	var defaults = {
        data: null,
		dataSource: '',
		id: 0,
		key: null
	};

    $.fn.questionnaire = function(options, callback) {

    	if ('function' == typeof(options)) {
    		callback = options;
    		options = null;
    	};
    	
    	if ('undefined' == callback) callback = null;
    	
    	return this.each(function() {
    	
	    	var self = this;
	    	var $this = $(this);
	    	var opts = $.extend( {}, defaults, options );
                
	    	var handleData = function(response) {
	    		
	    		// First node is info about the Questionnaire
	    		if ('undefined' != typeof(response[0])) $this.data('info', response[0]);
	    		
	    		$this.empty();
	    		
	    		// Create each root question
	    		var total = 0;
	    		for (var j = 1; j < response.length; j++) {
	    			if ('undefined' != typeof(response[j].type) && 'section' == response[j].type) {
	    				var $header = $('<header class="container">'+response[j].title+'</header>').appendTo($this);
	    				if ('undefined' != typeof(response[j].subheading)) $header.append('<br /><small>'+response[j].subheading+'</small>');
	    				continue;
	    			}
	    			$('<section data-question-number="'+total+'"></section>').question(response[j]).appendTo($this);
	    			total++;
	    		};
	    		
	    		// Determine when root questions have been changed, update the progress bar
	    		$this.find('.question').on('valueChange', function(event, _value, _element, _opts) {
	    			var $_element = $(_element);
	    			if ($_element.closest('.sub_questions').length) return;
	    			var id = parseInt($_element.closest('section').data('question-number'));
	    			var amount = Math.ceil( ((id + 1) * (1 / total)) * 100 ) + '%';
	    			$('.percent-finished-amount').animate({
	    				width: amount
	    			})
	    			$('.percent-finished-number').text(amount);
	    		});
		    	
	    	};
                                             
            if (opts.dataSource && opts.dataSource.length) {
            
                $.ajax({
                    url: opts.dataSource + opts.id,
                    jsonp: "callback",
                    dataType: "jsonp",
                    data: {},
                    success: function( response ) {
                        handleData(response)
                        if (callback) callback();
                    },
                    error: function( error ) {
                        alert('There was a problem trying to get the dataSource');
                        console.log(error);
                        if (callback) callback();
                    }
                });
            
            } else if (opts.data && opts.data.length) {
                
                handleData(opts.data);
                if (callback) callback();
                
            } else {
                
                alert('Missing data or dataSource');
                return;
                
            }
	    	
    	});	
	    	
    };

}( jQuery ));
