 
#include <stdio.h>
#include <stdlib.h>
#include "contiki.h"
#include "contiki-net.h"
#include "rest-engine.h"
#include "dev/button-sensor.h"
//#include "dev/leds.h"

static int value = 10;

void res_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
void res_event_post_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void event_handler();													//handler risorsa evento

void res_per_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void per_handler();													//handler risorsa periodica

EVENT_RESOURCE(resource_example, "title=\"Resource\";rt=\"Text\"", res_event_get_handler, res_event_post_handler, NULL, NULL, event_handler);   //event_handler è associato ad un generico evento
PERIODIC_RESOURCE(resource_per, "title=\"Resource Periodic\";rt=\"Text\"", res_per_get_handler, NULL, NULL, NULL, 1000 * CLOCK_SECOND, per_handler); //pre_handler è associato ad un evento periodico, il penultima parametro indica il periodo dell'evneto

void
res_event_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){

	/* Populat the buffer with the response payload*/
	char message[20];
	int length = 20;

	sprintf(message, "VALUE:%03u", value);					//valore delle risorsa nel payload
	length = strlen(message);
	memcpy(buffer, message, length);

	REST.set_header_content_type(response, REST.type.TEXT_PLAIN); 
	REST.set_header_etag(response, (uint8_t *) &length, 1);
	REST.set_response_payload(response, buffer, length);
}

void
res_event_post_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){


  int new_value, len;
  const char *val = NULL;
     
  len=REST.get_post_variable(request, "value", &val);				//carica il valore passato per post variable nella risorsa
     
  if( len > 0 ){
     new_value = atoi(val);	
     value = new_value;
     REST.set_response_status(response, REST.status.CREATED);
  } else {
     REST.set_response_status(response, REST.status.BAD_REQUEST);
  }
}

//queste due funzioni sono associate ad eventi generici 

static void
event_handler()
{
  /* Do the update triggered by the event here, e.g., sampling a sensor. */			
  ++value;

    /* Notify the registered observers which will trigger the tget_handler to create the response. */
    REST.notify_subscribers(&resource_example);
  
}


//periodic handler
void
res_per_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){

	/* Populat the buffer with the response payload*/
	char message[20];
	int length = 20;

	sprintf(message, "VALUE:%03u", value);
	length = strlen(message);
	memcpy(buffer, message, length);

	REST.set_header_content_type(response, REST.type.TEXT_PLAIN); 
	REST.set_header_etag(response, (uint8_t *) &length, 1);
	REST.set_response_payload(response, buffer, length);
}

//event handler 
static void
per_handler()
{
  /* Do the update triggered by the event here, e.g., sampling a sensor. */
  ++value;

    /* Notify the registered observers which will trigger the tget_handler to create the response. */
    REST.notify_subscribers(&resource_per);
  
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
  rest_activate_resource(&resource_per, "periodic");
  while(1) {
    PROCESS_WAIT_EVENT();
    if(ev == sensors_event && data == &button_sensor){
    	//leds_toggle(LEDS_ALL);
    	printf("Button pressed\n");
	resource_example.trigger();						//esecuzione evento della risorsa evento 
    }
  }
  PROCESS_END();
}
/*---------------------------------------------------------------------------*/
