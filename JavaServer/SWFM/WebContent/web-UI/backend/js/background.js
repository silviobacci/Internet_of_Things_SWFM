var size = 528;

function BACKGROUND(p) {
	this.canvas = $("#river-ov");
	this.unable = $("#unable_quad")[0];
	this.container1 = $("#canvas-left-container");
	this.container2 = $("#canvas-river-ov");
	this.context = this.canvas[0].getContext("2d");
	
	this.background_texture = $("#background_texture")[0];
	
	var min_dim = this.container1.innerWidth() < this.container1.innerHeight() ? this.container1.innerWidth() : this.container1.innerHeight();
	
	if(min_dim < size) {
		this.scaling_factor = min_dim / size;
		
		this.canvas[0].width = min_dim;
		this.canvas[0].height = min_dim;
		
		this.context.scale(this.scaling_factor, this.scaling_factor);
	}
	else {
		this.scaling_factor = 1;
		
		this.canvas[0].width = size;
		this.canvas[0].height = size;
	}
}

BACKGROUND.prototype.create_placeholder = function () {
	this.context.drawImage(this.unable, 0, 0, this.canvas[0].width, this.canvas[0].height);
}

BACKGROUND.prototype.draw_background = function () {
	this.context.drawImage(this.background_texture, 0, 0, this.background_texture.width, this.background_texture.height);
}
