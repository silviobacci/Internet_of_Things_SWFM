class Overlay {
	constructor(dim, sf) {
		this.canvas = $("#overlay");
		this.context = this.canvas[0].getContext("2d");
		
		this.canvas[0].width = dim;
		this.canvas[0].height = dim;
		
		if((this.scaling_factor = sf) != 1)
			this.context.scale(this.scaling_factor, this.scaling_factor);
	}
	
	create_placeholder() {
		this.context.clearRect(0, 0, this.canvas[0].width, this.canvas[0].height);
	}
}