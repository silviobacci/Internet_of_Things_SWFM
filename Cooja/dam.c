#include <stdio.h>
#include <stdlib.h>
#include "contiki.h"
#include "contiki-net.h"
#include "rest-engine.h"
#include "json_getter.c"
#include "types.h"
#include "sys/etimer.h" // Include etimer
#include "dev/serial-line.h"

/****** global variables ******/
static dam_state		d_state;
static unsigned int 		accept = -1;
static char 			j_message[MESSAGE_SIZE];
static struct jsonparse_state 	parser;	

/****** resources handlers ******/
void res_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
void res_event_post_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
void gps_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

/****** resources ******/
EVENT_RESOURCE(resource_example, "title=\"Resource\";rt=\"Dam\"", res_event_get_handler, res_event_post_handler, NULL, NULL, NULL);
EVENT_RESOURCE(gps, "title=\"Resource\";rt=\"gps\"", gps_event_get_handler, NULL, NULL, NULL, NULL);

/****** Handlers ******/

/********************************************
	gps get-handler
********************************************/
void gps_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	accept = -1;
  	REST.get_header_accept(request, &accept);							//retrieve accepted options
	
	sprintf(j_message,"{\"%s\":%d,\"%s\":%d}",str(gps_x),d_state.gpsx,str(gps_y),d_state.gpsy);
	
	REST.set_header_content_type(response,  REST.type.APPLICATION_JSON);			//set header content format
	REST.set_response_payload(response, j_message, strlen(j_message));
}

/********************************************
	Sensor get-handler
********************************************/
void res_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	accept = -1;

		
	sprintf(j_message,"{\"%s\":\"%s\"}", str(state),d_state.dam_state);	
	memcpy(buffer, j_message,strlen(j_message));

	REST.set_header_content_type(response,  REST.type.APPLICATION_JSON);	//set header content format
	REST.set_response_payload(response, buffer, strlen(j_message));

}

/********************************************
	Sensor post-handler
********************************************/
void res_event_post_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	int len;
	const char *val = NULL;
	char *message = NULL;	
	accept = -1;

	REST.get_header_accept(request, &accept);							//getting request
     
  	if(accept == REST.type.APPLICATION_JSON) {							//select and create the correct format: JSON
		struct jsonparse_state	parser;				
		
		len=REST.get_post_variable(request, "json", &val);					//get post variable (json format)			
	
		if( len > 0 && val[len-1]=='}'){							//check post parameter validity
			jsonparse_setup(&parser, val, len);
			
			jparse_and_store(&parser);

		 }else 
		     REST.set_response_status(response, REST.status.BAD_REQUEST);
				
		memcpy(buffer, message, MESSAGE_SIZE);
		REST.set_header_content_type(response,  REST.type.TEXT_PLAIN);				//set header content format
		REST.set_response_payload(response, buffer, MESSAGE_SIZE);
	}
}

/****** Utilities functions ******/

/********************************************
	update module position 
********************************************/
void update_position(int x, int y){
	if(d_state.gpsx != x || d_state.gpsy != y ){	
		d_state.gpsx = x;
		d_state.gpsy = y;
		
		REST.notify_subscribers(&gps);		
	}
}


void jparse_and_store(struct jsonparse_state *parser ){
	json_get_string(parser, str(state), d_state.dam_state,DAM_STATE_SIZE);
	REST.notify_subscribers(&resource_example);
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
PROCESS_THREAD(server, ev, data){
 	PROCESS_BEGIN();
	
	static struct etimer gps_timer;		
	
	uart0_set_input(serial_line_input_byte);
	serial_line_init();
								       
	rest_init_engine();
	rest_activate_resource(&resource_example, "Dam");
	rest_activate_resource(&gps, "gps");

	etimer_set(&gps_timer, CLOCK_SECOND * POS_SAMPLING_PERIOD);
	
	while(1){
		PROCESS_WAIT_EVENT();

 		if(etimer_expired(&gps_timer)){
			printf("g\n");
			if(store_gps(ev,(char *)data) != ERROR)
				etimer_reset(&gps_timer);
	
		}
	}
	PROCESS_END();
}
