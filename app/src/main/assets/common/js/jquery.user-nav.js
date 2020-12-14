(function( $ ) {

	var defaults = {
		user: {},
		facebookLogoutCallback: null
	};

    $.fn.userNav = function(options, callback) {

    	if ('function' == typeof(options)) {
    		callback = options;
    		options = null;
    	};
    	
    	if ('undefined' == callback) callback = null;
    	
    	return this.each(function() {
    	
	    	var self = this;
	    	var $this = $(this);
	    	var opts = $.extend( {}, defaults, options );
	    	
	    	$this.data('user', opts.user);
	    	
	    	if ('undefined' != typeof(opts.user.name)) {

		 		var $userNav = $('<div class="dropdown"></div>');
		 		$userNav.append('<a class="btn dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">'+opts.user.name+'</a>');
		  		if (opts.user.source == 'Facebook') {
		  			$userNav.find('.dropdown-toggle').append('<img src="http://graph.facebook.com/' + opts.user.id + '/picture?type=normal" class="rounded float-left social-icon" alt="Social icon" />');
		  		}
		 		$userNav.append('<div class="dropdown-menu" aria-labelledby="dropdownMenuButton"><a class="dropdown-item" href="javascript:void(null);">Logout or switch user</a></div>');
	
		 		$this.prepend($userNav);
		 		
		 		$userNav.find('.dropdown-item:first').on('click', function() {
		 			if (opts.user.source == 'Facebook') {
		 				if (opts.facebookLogoutCallback) opts.facebookLogoutCallback();
		 			} else {
		 				document.location.reload();
		 			}
		 		});
		 		
	    	};
	 		
    	});	
	    	
    };

}( jQuery ));