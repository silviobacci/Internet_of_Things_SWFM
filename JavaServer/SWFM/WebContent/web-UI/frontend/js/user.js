class User {
	constructor(p) {
		this.login_button = $("#btn-login");
		this.signup_button = $("#btn-signup");
		this.backend_button = $("#btn-backend");
		
		this.form_login = $("#form-login");
		this.form_signup =  $("#form-signup");
		this.navbar_brand =  $(".navbar-brand");
		this.form_button_login = $("#login-button");
		
		this.login_error_title = $(".login-error-title");
		this.login_error_text = $(".login-error-text");
		this.alert_login = $(".alert-login");
		this.form_button_signup = $("#signup-button");
		
		this.signup_error_title = $(".signup-error-title");
		this.signup_error_text = $(".signup-error-text");
		this.alert_signup = $(".alert-signup");
		
		this.signup_success_title = $(".signup-success-title");
		this.signup_success_text = $(".signup-success-text");
		this.alert_signup_success = $(".alert-signup-succ");
		
		this.secondary_button = $(".btn-secondary");
		this.form_signup_to_disable = $("#form-signup :input");
		
		this.navbar_brand.attr("href", rel_fron_path);
		this.form_login.vindicate("init");
		this.form_signup.vindicate("init");
		
		this.form_button_login.off("click");
		this.form_button_signup.off("click");
		
		var self = this;
		
		this.form_button_login.on("click", function(ev) {self.login()});
		this.form_button_signup.on("click", function(ev) {self.signup()});
	}
	
	success(reply) {
		if (reply.error == false) {
			this.login_button.hide();
			this.signup_button.hide();
		}
		else
		    this.error(reply);
	}
	
	error(reply) {
		this.backend_button.hide();
	}
	
	get_user_data() {
		ajax_get_req(redirect, this, this.success, this.error);
	}
	
	login_success(reply) {
		if (reply.error == false)
			window.location.replace(rel_back_path);
	    else
	    		this.login_error(reply);
	}
	
	login_error(reply) {
		this.login_error_title.html("Error: ");
		this.login_error_text.html(reply.message);
		this.alert_login.removeClass("d-none");
	    this.alert_login.addClass("show");
	}
	
	login() {
		this.form_login.vindicate("validate");
		var payload = this.form_login.serialize();
		ajax_post_req(login, payload, this, this.login_success, this.login_error);
	}
	
	signup_success(reply) {
		if (reply.error == false) {
			var payload = this.form_signup.serialize();
			this.signup_success_title.html("Success: ");
			this.signup_success_text.html(reply.message);
			this.alert_signup_success.removeClass("d-none");
		    this.alert_signup_success.addClass("show");
		    this.secondary_button.prop('disabled', true);
		    this.form_signup_to_disable.prop('disabled', false);
		    
			ajax_post_req(login, payload, this, this.login_success, this.login_error);
		}
	    else
	    		this.signup_error(reply);
	}
	
	signup_error(reply) {
		this.signup_error_title.html("Error: ");
		this.signup_error_text.html(reply.message);
		this.alert_signup.removeClass("d-none");
	    this.alert_signup.addClass("show");
	}
	
	signup() {
		this.form_signup.vindicate("validate");
		var payload = this.form_signup.serialize();
		ajax_post_req(signup, payload, this, this.signup_success, this.signup_error);
	}
}
