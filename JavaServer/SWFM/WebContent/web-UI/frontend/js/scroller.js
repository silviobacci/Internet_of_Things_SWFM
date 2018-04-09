class Scroller {
	constructor(p) {
		this.no_scroll = $(".no-scroll");
		this.yes_scroll = $(".yes-scroll");
		
		this.fullpage = $('#fullpage');
		this.mobile_div = $('.mobile-div');
		this.row_heading = $('.row-heading:not(:first)');
		
		if(window.matchMedia("(min-width: 768px)").matches){
			this.mobile_div.remove();
			this.fullpage.fullpage();
		}
		else
			this.row_heading.hide();
		
		this.no_scroll.off("shown.bs.modal");
		this.yes_scroll.off("click");
		
		var self = this;
		
		this.no_scroll.on("shown.bs.modal", function(ev) {self.stop_scroll()});
		this.yes_scroll.on("click", function(ev) {self.scroll()});
	}

	scroll() {
		$.fn.fullpage.setMouseWheelScrolling(true);
		$.fn.fullpage.setAllowScrolling(true);
	}

	stop_scroll() {
		$.fn.fullpage.setMouseWheelScrolling(false);
		$.fn.fullpage.setAllowScrolling(false);
	}
}
