(function( $ ) {

	var defaults = {
		info: null
	};

    $.fn.feedback = function(options) {

    	return this.each(function() {
    	
	    	var self = this;
	    	var $this = $(this);
	    	var opts = $.extend( {}, defaults, options );
	    	var $modal = $('#feedbackModal');
	    	
	    	var getTitles = function($el, include_values, titles) {
	    		if ('undefined' == typeof(include_values)) include_values = false;
	    		if ('undefined' == typeof(titles)) titles = [];
	    		titles.push($el.find('.title:first').text());
	    		if (include_values) {
		    		var values = $el.closest('section').data('values');
		    		if ('undefined' != typeof(values)) titles.push('[' + values + ']');
	    		}
	    		return ($el.closest('.sub_questions').length) ? getTitles($el.closest('.sub_questions').closest('.question'), include_values, titles) : titles.reverse();
	    	};

	    	var question_id = parseInt($this.find('form:first').data('question-id'));
	    	var questions = getTitles($this);
	    	$modal.find('#feedback-question').html('"' + questions.join('"<br />"') + '"');
	    	$modal.modal('show');
	    	
	    	$modal.find('button[type="button"]').off('click').on('click', function() {
	    		$modal.find('textarea').val('');
	    		$modal.modal('hide');
	    	});
	    	
	    	$modal.find('form:first').data('info', opts.info).data('question_id', question_id).data('question', getTitles($this, true).join(' > ')).off('submit').on('submit', function() {
	    		var $form = $(this);
	    		var info = $form.data('info');
	    		var question_id = $form.data('question_id');
	    		var question = $form.data('question');
	    		var feedback = $form.find('textarea[name="feedback"]').val();
	    		if (!feedback.length) {
	    			alert('Please provide feedback in the form before submitting.');
	    			return false;
	    		}
	    		// Send payload
	    		var json = {
	    			action:'doSaveFeedback',
	    			question_id:question_id,
	    			question:question,
	    			feedback:feedback
	    		};
	    		$.ajax({
	    			url: info.handler,
	    			type: "POST",
	    			dataType: 'json',
	    			contentType: 'application/json',
	    			data: JSON.stringify(json),
	    			success: function success(data) {
	    				if ('undefined' != typeof(data.error)) {
	    					alert('There was an error attempting to save feedback: '+data.error);
	    					return;
	    				};
	    	    		$modal.find('textarea').val('');
	    	    		$modal.modal('hide');
	    			}
	    		});
	    		return false;
	    	});
	    	
    	});	
	    	
    };

}( jQuery ));