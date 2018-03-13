#define 	ERROR 			-1
#define 	MESSAGE_SIZE		40
#define 	FIXED_STEP		5
#define 	RES_CHANGE		4
#define 	DAM_STATE_SIZE		10
#define		MIN_LEVEL_DETECTABLE	30
#define		MAX_LEVEL_DETECTABLE	500
#define 	LEVEL_SAMPLING_PERIOD	10
#define 	POS_SAMPLING_PERIOD	25

#define str(s) 		#s

const char *w_l,*w_t,*evolution,*state,*closed,*open,*get_gps,*gps_x,*gps_y;


typedef struct{
	int gps_x;
	int gps_y;
	int evolution;
	unsigned int water_level;
	unsigned int water_level_threshold;
} sensor_state;


typedef struct{
	int gpsx;
	int gpsy;
	char dam_state[7];
} dam_state;

