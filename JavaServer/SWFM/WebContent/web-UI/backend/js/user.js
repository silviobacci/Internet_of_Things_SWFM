class User {
	constructor(p) {
		this.logout_button = $("#btn-logout > a");
		this.navbar_avatar = $(".nav-avatar");
		this.navbar_brand = $(".navbar-brand");
		this.card_avatar = $(".card-avatar");
		this.card_name = $(".card-name");
		this.card_text = $(".card-text");
		this.cover_img = $(".cover-img");
		this.admin_container = $(".admin-to-hide");
		
	}
	
	prepare_page(userdata) {
		var self = this;
		this.logout_button.on("click", function(ev){self.logout()});
		this.navbar_avatar.attr("src", img_svr_path + userdata.avatar);
		this.navbar_brand.attr("href", rel_fron_path);
		this.card_avatar.attr("src", img_svr_path + userdata.avatar);
		this.card_name.html(userdata.name + " " + userdata.surname);
		this.cover_img.css("background-image", "url(" + img_svr_path + userdata.cover + ")");
		this.is_admin = userdata.admin;
		
		if(this.is_admin == true){
			this.card_text.html("You are an administrator. You can act directly on our dams in order control the water flows.");
			this.admin_container.show();
		}
		else{
			this.card_text.html("You are a standard user so we can simply observe an overview of the current state of the water flows.");
			this.admin_container.hide();
		}
	}
	
	success(reply) {
		if (reply.error == false)
		    this.prepare_page(reply.message);
		else
		    this.error(reply);
	}
	
	error(reply) {
		alert("Server unreachable: " + reply.message);
	    window.location.replace(rel_fron_path);
	}
	
	get_user_data() {
		ajax_get_req(redirect, this, this.success, this.error);
	}
	
	logout_success(reply) {
		window.location.replace(rel_fron_path);
	}
	
	logout_error(reply) {
		alert("Server unreachable: " + reply.message);
	}
	
	logout() {
		ajax_get_req(logout, this, this.logout_success, this.logout_error);
	}
}
