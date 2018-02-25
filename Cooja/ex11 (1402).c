#include <stdio.h>
#include <stdlib.h>
#include "contiki.h"
#include "contiki-net.h"
#include "rest-engine.h"
#include "dev/button-sensor.h"
#include "json_getter.c"


static int value =10;
int invalidator = 33;

void res_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
void res_event_post_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void event_handler();



EVENT_RESOURCE(resource_example, "title=\"Resource\";rt=\"Text\"", res_event_get_handler, res_event_post_handler, NULL, NULL, event_handler);


void
res_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){

	/* Populat the buffer with the response payload*/
	/*char message[40];
	int length = 40;
	unsigned int accept = -1;
  	const char *val = NULL;
  	int new_value, len;
  	REST.get_header_accept(request, &accept);	
	//sprintf(message, "VALUE:%d",accept);				//retrieve accepted options
	if(accept == -1 || accept == REST.type.TEXT_PLAIN) {				//select and create the correct format: plain text
		
     
  		len=REST.get_post_variable(request, "value", &val);
     
		  if( len > 0 ){
		     new_value = atoi(val);	
		     value = new_value;
		     REST.set_response_status(response, REST.status.CREATED);
		  } else 
		     REST.set_response_status(response, REST.status.BAD_REQUEST);
		  
				
		sprintf(message, "%d ", value);
		//sprintf(message, "VALUE:%03u",accept);
		length = strlen(message);
		
/*
		REST.set_header_content_type(response, REST.type.TEXT_PLAIN); 		//set header content format
		REST.set_response_payload(response, buffer, length);
	
	} else if(accept == REST.type.APPLICATION_JSON) {				//select and create the correct format: XML
		struct jsonparse_state	*parser;					
		
		len=REST.get_query_variable(request, "json", &val);			//get post variable
     
		  if( len > 0 ){
		     sprintf(message, val);
		     jsonparse_setup(parser, val, len);					//json parser setup 
		  } else 
		     REST.set_response_status(response, REST.status.BAD_REQUEST);
		
		length = strlen(message);
		
		memcpy(buffer, message, length);
		buffer[length] = '\0';

		REST.set_header_content_type(response,  REST.type.TEXT_PLAIN);//REST.type.APPLICATION_JSON);	//set header content format
		REST.set_response_payload(response, buffer, length);
	} else{
		REST.set_response_status(response, REST.status.NOT_ACCEPTABLE);
		    const char *msg = "Supporting content-types text/plain, application/xml";
		    REST.set_response_payload(response, msg, strlen(msg));
	}*/
}

void
res_event_post_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){


  int new_value, len,number;
  const char *val = NULL;
  char message[90];
  int length = 90;
  unsigned int accept = -1;

//"fooooooooooooooooooooooooooo":"si","numero":666,"fdittizio":"si","nome":"waters"

  REST.get_header_accept(request, &accept);
     
  if(accept == REST.type.APPLICATION_JSON) {				//select and create the correct format: XML
		struct jsonparse_state	parser;					
		
		len=REST.get_post_variable(request, "json", &val);			//get post variable (json format)
     		 printf("received from json: %c length:%d \n",val[len-1],len);	
		  if( len > 0 ){
			jsonparse_setup(&parser, val, len);
	//	 printf("received from json setup: %s \n",val);	
		   json_get_int(&parser, "numero", &number);
	//	    printf("Searching for int:%d\n",number);
			json_get_string(&parser, "nome", message, 40);
	//	     printf("Searching for string:%s\n",message);
		
		     
 	//	    printf("received from json: %s getting name:%s and number:%d \n",val,message,number);	
		     

		    // sprintf(message,"e%d",jsonparse_get_value_as_int(&parser));
		
		  } else 
		     REST.set_response_status(response, REST.status.BAD_REQUEST);
		 
	//	sprintf(message, parser.json);
				
		length = strlen(message);
		
		memcpy(buffer, message, length);
		buffer[length] = '\0';

		REST.set_header_content_type(response,  REST.type.TEXT_PLAIN);//REST.type.APPLICATION_JSON);	//set header content format
		REST.set_response_payload(response, buffer, length);
 
}
}

static void
event_handler()
{
  /* Do the update triggered by the event here, e.g., sampling a sensor. */
  ++value;

    /* Notify the registered observers which will trigger the tget_handler to create the response. */
    REST.notify_subscribers(&resource_example);
  
}

void
res_per_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){

	/* Populat the buffer with the response payload*/
	/*char message[20];
	int length = 20;

	/*sprintf(message, "VALUE:%03u", value);
	length = strlen(message);
	memcpy(buffer, message, length);

	/*REST.set_header_content_type(response, REST.type.TEXT_PLAIN); 
	REST.set_header_etag(response, (uint8_t *) &length, 1);
	REST.set_response_payload(response, buffer, length);*/
}


/*---------------------------------------------------------------------------*/
PROCESS(server, "Server process");
AUTOSTART_PROCESSES(&server);
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(server, ev, data)
{
  PROCESS_BEGIN();
  SENSORS_ACTIVATE(button_sensor);
  rest_init_engine();
  rest_activate_resource(&resource_example, "example");

  while(1) {
    PROCESS_WAIT_EVENT();
    if(ev == sensors_event && data == &button_sensor){
    	//leds_toggle(LEDS_ALL);
    	printf("Button pressed\n");
	resource_example.trigger();
    }
  }
  PROCESS_END();
}
