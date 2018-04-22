#include <stdio.h>
#include <stdlib.h>
#include "contiki.h"
#include "contiki-net.h"
#include "dev/serial-line.h"
#include "rest-engine.h"
#include "json_getter.c"
#include "types.h"
#include "sys/etimer.h" 			
#include "dev/serial-line.h"
#include "node-id.h"

/****** global variables ******/
static sensor_state	s_state;
static int 		reference; 
static char 		j_message[MESSAGE_SIZE];
static char 		resource_name[MESSAGE_SIZE];
static struct 		jsonparse_state	parser;	
static int 		tmp;

/****** resources handlers ******/
void res_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
void res_event_post_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
void gps_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

/****** resources ******/
EVENT_RESOURCE(resource_example, "title=\"Resource\";rt=\"Sensor\"", res_event_get_handler, res_event_post_handler, NULL, NULL, NULL);
EVENT_RESOURCE(gps, "title=\"Resource\";rt=\"gps\"", gps_event_get_handler, NULL, NULL, NULL, NULL);



/****** Handlers ******/

/********************************************
	Gps get-handler
********************************************/
void gps_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	sprintf(j_message,"{\"%s\":%d,\"%s\":%d}",str(gps_x),s_state.gps_x,str(gps_y),s_state.gps_y);
	
	REST.set_response_payload(response, j_message, strlen(j_message));
}

/********************************************
	Dam get-handler
********************************************/
void res_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	sprintf(j_message,"{\"%s\":%d,\"%s\":%d,\"%s\":%d,\"%s\":%d,\"%s\":%d}",str(w_l), s_state.water_level,str(w_t), s_state.water_level_threshold, str(evo), s_state.evolution,
										str(min), s_state.min, str(max), s_state.max);	
	REST.set_response_payload(response, j_message, strlen(j_message));
	
}

/********************************************
	Dam post-handler
********************************************/
void res_event_post_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	const char *val = NULL;			
	
	tmp = REST.get_post_variable(request, "json", &val);					//get post variable (json format)			
	if( tmp > 0 && val[tmp-1] == '}'){							//check post parameter validity
		jsonparse_setup(&parser, val, tmp);
		jparse_and_store(&parser);
		REST.notify_subscribers(&resource_example);
	}
}

/****** Utilities functions ******/

/********************************************
	update module position 
********************************************/
void update_position(int x, int y){
	if(s_state.gps_x != x || s_state.gps_y != y ){	
		s_state.gps_x = x;
		s_state.gps_y = y;
		REST.notify_subscribers(&gps);
	}
}

/********************************************
	check if dam state is changed
********************************************/
void check_resource_changed(){
	if(abs(s_state.water_level-reference) >= RES_CHANGE  ){
		REST.notify_subscribers(&resource_example);
		reference = s_state.water_level;
	}
}

/********************************************
	Simulate water level evolution
********************************************/
void state_step(){
	int8_t random =  abs((rand() %  FIXED_STEP)) ;
	int8_t step = s_state.evolution  * random;

	if(s_state.water_level + step < MAX_LEVEL_DETECTABLE && s_state.water_level + step > MIN_LEVEL_DETECTABLE )
		s_state.water_level = s_state.water_level + step;	
	
	check_resource_changed();
}


/****************************************************************
	update the resource state according to the received JSON
****************************************************************/
void jparse_and_store(struct jsonparse_state *parser ){
	

	if(json_get_int(parser, str(w_l), &tmp) != ERROR){
		reference = tmp;	
		s_state.water_level = tmp;	
	}
	if(json_get_int(parser, str(w_t), &tmp) != ERROR)
		s_state.water_level_threshold = tmp;

	if(json_get_int(parser, str(evo), &tmp) != ERROR)
		s_state.evolution = tmp;
	
	if(json_get_int(parser, str(min), &tmp) != ERROR)
		s_state.min = tmp;

	if(json_get_int(parser, str(max), &tmp) != ERROR)
		s_state.max = tmp;

}

/********************************************
	Acquire and store gps coordinates
********************************************/
int  store_gps(process_event_t ev,char *data){
	
	if(ev == serial_line_event_message){
	
		jsonparse_setup(&parser,data,strlen(data));
		
		if(json_get_int(&parser,"x", &tmp_x) != ERROR && json_get_int(&parser,"y", &tmp_y) != ERROR )	
			update_position(tmp_x,  tmp_y);
			return 0;		
	}   
		return ERROR;
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

	etimer_set(&sampling_timer, CLOCK_SECOND * LEVEL_SAMPLING_PERIOD);
	etimer_set(&gps_timer, CLOCK_SECOND * POS_SAMPLING_PERIOD);
	
	rest_init_engine();
	sprintf(resource_name,"Sensor_%d",node_id);
	rest_activate_resource(&resource_example, resource_name);
	rest_activate_resource(&gps, "gps");

	while(1) {

	PROCESS_WAIT_EVENT();	
		if(etimer_expired(&sampling_timer) && s_state.water_level > 0 ){		
			state_step();
			etimer_reset(&sampling_timer);
		}if(etimer_expired(&gps_timer)){
			printf("g\n");
			if(store_gps(ev,(char *)data) != ERROR)
				etimer_reset(&gps_timer);	
		}
	
	PROCESS_END();
	}
}



