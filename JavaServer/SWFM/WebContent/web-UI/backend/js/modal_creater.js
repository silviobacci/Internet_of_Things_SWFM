class ModalCreater {
	constructor(a, user) {
		this.ae = a;
		this.is_admin = user;
		this.button = $("#button-close");
		this.modal_container = window.parent.$("#modal");
		this.admin_container = $(".admin-to-hide");
		
		if(this.is_admin == true)
			this.admin_container.show();
		else
			this.admin_container.hide();
		
		var delay = 500;
		
		setTimeout(this.modal_show_handler.bind(this), delay, this.ae, this.is_admin);
	}
	
	modal_show_handler(ae, is_admin) {
		this.modal = new Modal(ae, is_admin);
		var self = this;
		this.button.on("click", function(ev) {self.modal_hide_handler()});
	}
	
	modal_hide_handler() {
		this.modal.dams.eventSource.close();
		this.modal.sensors.eventSource.close();
		if(this.modal.history.eventSource != undefined)
			this.modal.history.eventSource.close();
		
		this.modal_container.modal("hide");
	}
}