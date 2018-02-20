#include <stdio.h>
#include <stdlib.h>
#include "contiki.h"
#include "contiki-net.h"
#include "rest-engine.h"
#include "json_getter.c"
#include "types.h"
#include "sys/etimer.h" // Include etimer


static sensor_state state;
static int reference; 
static int invalidator = 63,initialized = 44;
void res_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
void res_event_post_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);


EVENT_RESOURCE(resource_example, "title=\"Resource\";rt=\"Sensor\"", res_event_get_handler, res_event_post_handler, NULL, NULL, NULL);

void check_resource_changed(){
	if(abs(state.water_level-reference) >= RES_CHANGE && state.evolution!=0){
		printf("notify \n");
		//REST.set_header_content_type(response,  REST.type.APPLICATION_JSON);
		REST.notify_subscribers(&resource_example);
		reference = state.water_level;
	}

}

void state_step(){
	int step = state.evolution * FIXED_STEP;
	int random =  abs((rand() %  10)) ;

	if(state.water_level + random *  step < MAX_LEVEL_DETECTABLE && state.water_level +  random *  step > MIN_LEVEL_DETECTABLE )
		state.water_level = state.water_level +  random *  step;	
	check_resource_changed();	
}

void
res_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	
	/* Populat the buffer with the response payload*/
	char *message = NULL;
	char j_message[MESSAGE_SIZE];
	unsigned int accept = -1;
  	REST.get_header_accept(request, &accept);					//retrieve accepted options
	if(  accept == REST.type.TEXT_PLAIN) {				//select and create the correct format: plain tex
		printf("efgb \n");
		//build text plain response
		REST.set_header_content_type(response, REST.type.TEXT_PLAIN); 		//set header content format
		REST.set_response_payload(response, buffer, MESSAGE_SIZE);
	
	} else if( accept == -1 || accept == REST.type.APPLICATION_JSON) {				//select and create the correct format: XML
		sprintf(j_message,"{\"%s\":%d,\"%s\":%d,\"%s\":%d}",str(w_l),state.water_level,str(w_t),state.water_level_threshold,str(evolution),state.evolution);	
		printf("sended:%s \n",j_message);
		memcpy(buffer, j_message,strlen(j_message));

		REST.set_header_content_type(response,  REST.type.APPLICATION_JSON);	//set header content format
		REST.set_response_payload(response, buffer, strlen(j_message));
	
	} else{
	printf("esle \n");
		REST.set_response_status(response, REST.status.NOT_ACCEPTABLE);
		//message = "Supporting content-types text/plain, application/xml";
		REST.set_response_payload(response, message, strlen(message));
	}
}

void jparse_and_store(struct jsonparse_state *parser ){
	int tmp;

	if(json_get_int(parser, str(w_flow), &tmp) != ERROR)
		state.water_flow = tmp;

	if(json_get_int(parser, str(w_l), &tmp) != ERROR){
		reference = tmp;	
		state.water_level = tmp;
		initialized = 1;
	}
	if(json_get_int(parser, str(w_t), &tmp) != ERROR)
		state.water_level_threshold = tmp;

	if(json_get_int(parser, str(evolution), &tmp) != ERROR){
		state.evolution = tmp;
	//	if(state.evolution != 0)
			//sstate.to_reach = state.water_level_threshold + state.evolution *  abs((rand() %  5)) * 0.1 * state.water_level_threshold;
	}
}

void res_event_post_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	int len;
	const char *val = NULL;
	char *message = NULL;	
	unsigned int accept = -1;

	REST.get_header_accept(request, &accept);							//getting request
     
  	if(accept == REST.type.APPLICATION_JSON) {							//select and create the correct format: JSON
		struct jsonparse_state	parser;				
		
		len=REST.get_post_variable(request, "json", &val);					//get post variable (json format)			
	
		if( len > 0 && val[len-1]=='}'){							//check post parameter validity
			jsonparse_setup(&parser, val, len);
			jparse_and_store(&parser);

			printf("level=%d ,threshold=%d, evolution=%d \n",state.water_level,state.water_level_threshold,state.evolution);
		
		 }else 
		     REST.set_response_status(response, REST.status.BAD_REQUEST);
				
								
		memcpy(buffer, message, MESSAGE_SIZE);	//building response
		REST.set_header_content_type(response,  REST.type.TEXT_PLAIN);//REST.type.APPLICATION_JSON);	//set header content format
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
 static struct etimer et; // Declare an etime
  etimer_set(&et, CLOCK_SECOND*7); // Set the timer
 // SENSORS_ACTIVATE(button_sensor);
  rest_init_engine();
  rest_activate_resource(&resource_example, "Sensor");

  while(1) {
	
	PROCESS_WAIT_EVENT();
	
	if(etimer_expired(&et)  && initialized){
		state_step();
		//printf("flow=%d ,level=%d ,threshold=%d, evolution=%d \n",state.level_to_reach,state.water_level,state.water_level_threshold,state.evolution);
		
	etimer_reset(&et);
	}
	/*if(etimer_expired(&et) && initialized == 1){
		state_step();
		printf("flow=%d ,level=%d ,threshold=%d, evolution=%d \n",state.water_flow,state.water_level,state.water_level_threshold,state.evolution);
		etimer_reset(&et); 
}*/



  }
  PROCESS_END();
}
