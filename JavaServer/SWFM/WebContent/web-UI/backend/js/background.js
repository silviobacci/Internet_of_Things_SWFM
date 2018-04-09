class Background {
	constructor(p) {
		this.size = 528;
		
		this.canvas = $("#river-ov");
		this.unable = $("#unable_quad")[0];
		this.container1 = $("#canvas-left-container");
		this.container2 = $("#canvas-river-ov");
		this.context = this.canvas[0].getContext("2d");
		
		this.background_texture = $("#background_texture")[0];
		
		var min_dim = this.container1.innerWidth() < this.container1.innerHeight() ? this.container1.innerWidth() : this.container1.innerHeight();
		
		if(min_dim < this.size) {
			this.scaling_factor = min_dim / this.size;
			
			this.canvas[0].width = min_dim;
			this.canvas[0].height = min_dim;
			
			this.context.scale(this.scaling_factor, this.scaling_factor);
		}
		else {
			this.scaling_factor = 1;
			
			this.canvas[0].width = this.size;
			this.canvas[0].height = this.size;
		}
		
		this.container1.width = this.canvas[0].width;
		this.container1.height = this.canvas[0].height;
	}
	
	create_placeholder() {
		this.context.drawImage(this.unable, 0, 0, this.canvas[0].width, this.canvas[0].height);
	}
	
	draw_background() {
		this.context.drawImage(this.background_texture, 0, 0, this.background_texture.width, this.background_texture.height);
	}
}
