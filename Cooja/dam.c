#include <stdio.h>
#include <stdlib.h>
#include "contiki.h"
#include "contiki-net.h"
#include "rest-engine.h"
#include "json_getter.c"
#include "types.h"
#include "sys/etimer.h" // Include etimer
#include "dev/serial-line.h"
//fare controllo sul content delle richieste 


static char dam_state[10] = "closed";
static int gpsx,gpsy;
static unsigned int accept = -1;
static char j_message[MESSAGE_SIZE];
static struct jsonparse_state parser;	

void res_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
void res_event_post_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
void gps_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

EVENT_RESOURCE(resource_example, "title=\"Resource\";rt=\"Dam\"", res_event_get_handler, res_event_post_handler, NULL, NULL, NULL);
EVENT_RESOURCE(gps, "title=\"Resource\";rt=\"gps\"", gps_event_get_handler, NULL, NULL, NULL, NULL);



void update_position(int x, int y){
	if(gpsx != x || gpsy != y ){	
		gpsx = x;
		gpsy = y;
		REST.notify_subscribers(&gps);
		//printf("changed %d %d \n",state.gps_x,state.gps_y);
	}
}


void
gps_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
 
	accept = -1;
  	REST.get_header_accept(request, &accept);							//retrieve accepted options
	 if( accept == -1 || accept == REST.type.APPLICATION_JSON) {				//select and create the correct format: JSON
		sprintf(j_message,"{\"%s\":%d,\"%s\":%d}",str(gps_x),gpsx,str(gps_y),gpsy);
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


void
res_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	
	/* Populate the buffer with the response payload*/
	char *message = NULL;
	
	accept = -1;
  	REST.get_header_accept(request, &accept);					//retrieve accepted options
	if(  accept == REST.type.TEXT_PLAIN) {						//select and create the correct format: plain tex
		REST.set_header_content_type(response, REST.type.TEXT_PLAIN); 		//set header content format
		REST.set_response_payload(response, buffer, MESSAGE_SIZE);
	
	} else if( accept == -1 || accept == REST.type.APPLICATION_JSON) {		//select and create the correct format: X	
		sprintf(j_message,"{\"%s\":\"%s\"}", str(state),dam_state);	
		memcpy(buffer, j_message,strlen(j_message));

		REST.set_header_content_type(response,  REST.type.APPLICATION_JSON);	//set header content format
		REST.set_response_payload(response, buffer, strlen(j_message));
	
	} else{
		REST.set_response_status(response, REST.status.NOT_ACCEPTABLE);
		message = "Supporting content-types text/plain, application/xml";
		REST.set_response_payload(response, message, strlen(message));
	}
}

void jparse_and_store(struct jsonparse_state *parser ){
	json_get_string(parser, str(state), dam_state,DAM_STATE_SIZE);
	REST.notify_subscribers(&resource_example);
}

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

/*---------------------------------------------------------------------------*/
PROCESS(server, "Server process");
AUTOSTART_PROCESSES(&server);
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(server, ev, data)
{
 
	PROCESS_BEGIN();
	uart0_set_input(serial_line_input_byte);
	serial_line_init();
	static struct etimer gps_timer;									       // Declare an etime
	static int tmp_x,tmp_y;					       // Set the timer
	rest_init_engine();
	rest_activate_resource(&resource_example, "Dam");
	rest_activate_resource(&gps, "gps");

	etimer_set(&gps_timer, CLOCK_SECOND * POS_SAMPLING_PERIOD);
	while(1){
		PROCESS_WAIT_EVENT();

 		if(etimer_expired(&gps_timer)){
			//PROCESS_WAIT_EVENT();	
			printf("get_gps\n");
			if(ev == serial_line_event_message){
				jsonparse_setup(&parser,(char *)data,strlen((char *)data));
				if(json_get_int(&parser,"x", &tmp_x) != ERROR && json_get_int(&parser,"y", &tmp_y) != ERROR )	
					update_position(tmp_x,  tmp_y);
				etimer_reset(&gps_timer);
			}	
		}
	}
	PROCESS_END();
}
