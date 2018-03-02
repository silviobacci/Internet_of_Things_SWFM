#include <stdio.h>
#include <stdlib.h>
#include "contiki.h"
#include "contiki-net.h"
#include "dev/serial-line.h"
#include "rest-engine.h"
#include "json_getter.c"
#include "types.h"
#include "sys/etimer.h" 				// Include etimer


static sensor_state s_state;
static int reference; 
static int initialized = 0;
static char j_message[MESSAGE_SIZE];
static struct jsonparse_state	parser;	
static	unsigned int accept = -1;
static int tmp;

void res_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
void res_event_post_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
void gps_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

EVENT_RESOURCE(resource_example, "title=\"Resource\";rt=\"Sensor\"", res_event_get_handler, res_event_post_handler, NULL, NULL, NULL);
EVENT_RESOURCE(gps, "title=\"Resource\";rt=\"gps\"", gps_event_get_handler, NULL, NULL, NULL, NULL);

void
gps_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
 
	accept = -1;
  	REST.get_header_accept(request, &accept);							//retrieve accepted options
	 if( accept == -1 || accept == REST.type.APPLICATION_JSON) {				//select and create the correct format: JSON
		sprintf(j_message,"{\"%s\":%d,\"%s\":%d}",str(gps_x),s_state.gps_x,str(gps_y),s_state.gps_y);
		//printf("sended:%s \n",j_message);
		//memcpy(buffer, j_message,strlen(j_message));

		REST.set_header_content_type(response,  REST.type.APPLICATION_JSON);			//set header content format
		REST.set_response_payload(response, j_message, strlen(j_message));
	
	}/* else{
		REST.set_response_status(response, REST.status.NOT_ACCEPTABLE);
		//message = "Supporting content-types text/plain, application/xml";
		REST.set_response_payload(response, message, strlen(message));
	}*/
}

void check_resource_changed(){
	if(abs(s_state.water_level-reference) >= RES_CHANGE && s_state.evolution!=0){
		//printf("notify \n");
		//REST.set_header_content_type(response,  REST.type.APPLICATION_JSON);
		REST.notify_subscribers(&resource_example);
		reference = s_state.water_level;
	}

}

void state_step(){
	int step = s_state.evolution ;
	int random =  abs((rand() %  FIXED_STEP)) ;

	if(s_state.water_level + random *  step < MAX_LEVEL_DETECTABLE && s_state.water_level +  random *  step > MIN_LEVEL_DETECTABLE ){
		s_state.water_level = s_state.water_level +  random *  step;	
	}
	check_resource_changed();	
}

void
res_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){

	// Populat the buffer with the response payload
	
	accept = -1;
  	REST.get_header_accept(request, &accept);							//retrieve accepted options
	/*if(  accept == REST.type.TEXT_PLAIN) {								//select and create the correct format: plain tex
		REST.set_header_content_type(response, REST.type.TEXT_PLAIN); 				//set header content format
		REST.set_response_payload(response, buffer, MESSAGE_SIZE);
	
	} else */if( accept == -1 || accept == REST.type.APPLICATION_JSON) {				//select and create the correct format: JSON
		sprintf(j_message,"{\"%s\":%d,\"%s\":%d,\"%s\":%d}",str(w_l),s_state.water_level,str(w_t),s_state.water_level_threshold,str(evolution),s_state.evolution);	
		//printf("sended:%s \n",j_message);
		//memcpy(buffer, j_message,strlen(j_message));

		REST.set_header_content_type(response,  REST.type.APPLICATION_JSON);			//set header content format
		REST.set_response_payload(response, j_message, strlen(j_message));
	
	}/* else{
		REST.set_response_status(response, REST.status.NOT_ACCEPTABLE);
		//message = "Supporting content-types text/plain, application/xml";
		REST.set_response_payload(response, message, strlen(message));
	}*/
}

void jparse_and_store(struct jsonparse_state *parser ){
	

	if(json_get_int(parser, str(w_l), &tmp) != ERROR){
		reference = tmp;	
		s_state.water_level = tmp;
		initialized = 1;
	}
	if(json_get_int(parser, str(w_t), &tmp) != ERROR)
		s_state.water_level_threshold = tmp;

	if(json_get_int(parser, str(evolution), &tmp) != ERROR)
		s_state.evolution = tmp;
}

void res_event_post_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	const char *val = NULL;
	accept = -1;

	REST.get_header_accept(request, &accept);							//getting request
     
  	if(accept == REST.type.APPLICATION_JSON) {							//select and create the correct format: JSON
					
		
		tmp = REST.get_post_variable(request, "json", &val);					//get post variable (json format)			
	
		if( tmp > 0 && val[tmp-1] == '}'){							//check post parameter validity
			jsonparse_setup(&parser, val, tmp);
			jparse_and_store(&parser);

			//printf("level=%d ,threshold=%d, evolution=%d \n",state.water_level,state.water_level_threshold,state.evolution);
		
		 }else 
		     REST.set_response_status(response, REST.status.BAD_REQUEST);
				
								
		//memcpy(buffer, message, MESSAGE_SIZE);							//building response
		//REST.set_header_content_type(response,  REST.type.APPLICATION_JSON);			//set header content format
		//REST.set_response_payload(response, buffer, MESSAGE_SIZE);
	}
}

void update_position(int x, int y){
	if(s_state.gps_x != x || s_state.gps_y != y ){	
		s_state.gps_x = x;
		s_state.gps_y = y;
		REST.notify_subscribers(&gps);
		//printf("changed %d %d \n",state.gps_x,state.gps_y);
	}
}



/*---------------------------------------------------------------------------*/
PROCESS(server, "Server process");
AUTOSTART_PROCESSES(&server);
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(server, ev, data) {
	PROCESS_BEGIN();
	uart0_set_input(serial_line_input_byte);
	serial_line_init();
	static struct etimer sampling_timer,gps_timer; 
	
	int x ;
	int y ;

etimer_set(&sampling_timer, CLOCK_SECOND * LEVEL_SAMPLING_PERIOD);
etimer_set(&gps_timer, CLOCK_SECOND * POS_SAMPLING_PERIOD);
rest_init_engine();
rest_activate_resource(&resource_example, "Sensor");
rest_activate_resource(&gps, "gps");



	while(1) {

	PROCESS_WAIT_EVENT();	
		if(etimer_expired(&sampling_timer) && initialized == 1 ){
			//printf("step \n");		
			state_step();
			etimer_reset(&sampling_timer);
		}else if(etimer_expired(&gps_timer)){
			//PROCESS_WAIT_EVENT();	
			printf("get_gps\n");
			if(ev == serial_line_event_message){
				jsonparse_setup(&parser,(char *)data,strlen((char *)data));
				if(json_get_int(&parser,"x", &x) != ERROR && json_get_int(&parser,"y", &y) != ERROR )	
					update_position(x,  y);
				etimer_reset(&gps_timer);
			}
		}
	
	PROCESS_END();
}
}


