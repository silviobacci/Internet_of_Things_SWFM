//------------------------------------------------------------------------------
// json_getter: jsonparse.h wrapper, provides json parsing functions
//------------------------------------------------------------------------------
#include <stdio.h>
#include "apps/json/jsonparse.h"

//------------------------------------------------------------------------------
// json_has_next: check if there is another value in the json string
//------------------------------------------------------------------------------
int json_has_next(struct jsonparse_state *state)
{
	return state->pos < state->len;
}

//------------------------------------------------------------------------------
// json_iterate_to: iterates over the json string values stopping where it is
// 		    equal to name parameter
//------------------------------------------------------------------------------
int json_iterate_to(struct jsonparse_state *state, char * name){
	char message[90],tmp[90];
	
	jsonparse_setup(state, state->json, state->len);			//parser reset	
	jsonparse_next(state);							
	sprintf(message,(char*)state->json+state->pos);

	while(strcmp(message, name) != 0){					//check if the current value is the requested one

		jsonparse_next(state);
		jsonparse_copy_value(state, message,10);			//current value
	
		if (!json_has_next(state)){
			//printf("Reached end of JSON!\n");
			return -1; 
		}		
	}

	jsonparse_next(state);
	jsonparse_copy_value(state, tmp,90);	
	//printf("value:%s \n",tmp);
	return 0;	
}

//------------------------------------------------------------------------------
// json_get_int: returns the integer value associated to the properties passed
//		    as parameter
//------------------------------------------------------------------------------
int json_get_int(struct jsonparse_state *state, char * name, int* value){
	if(json_iterate_to(state,name) != 0)					//check if valuen name is present
		return -1; 
	
	if( jsonparse_get_type(state) == JSON_TYPE_PAIR){			//check if the value type is correct	
		(*value) = jsonparse_get_value_as_int(state);
		return 0; 
	}else
		return -1;
		
}

//------------------------------------------------------------------------------
// json_get_string: returns the string value associated to the properties passed
//		    as parameter
//------------------------------------------------------------------------------
int json_get_string(struct jsonparse_state *state, char * name, char* value, int len){
	if(json_iterate_to(state,name) != 0)					//check if value name is present
		return -1; 
	
	if( jsonparse_get_type(state) == JSON_TYPE_PAIR){			//check if the value type is correct	
		jsonparse_copy_value(state, value, len);
		return 0; 
	}else
		return -1;
		
}
