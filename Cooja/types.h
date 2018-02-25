#define 	W_INCREASE 		1
#define 	W_DECREASE 		-1
#define 	W_STATIONARY 		0
#define 	ERROR 			-1
#define 	MESSAGE_SIZE		100
#define 	FIXED_STEP		1
#define 	EQUAL_THRESHOLD 	20
#define 	RES_CHANGE		5
#define 	DAM_STATE_SIZE		10
#define		MIN_LEVEL_DETECTABLE	30
#define		MAX_LEVEL_DETECTABLE	500
#define 	LEVEL_SAMPLING_PERIOD	10
#define 	POS_SAMPLING_PERIOD	25

#define str(s) 		#s

const char *w_l,*w_t,*evolution,*dam,*closed,*open,*get_gps,*gps_x,*gps_y;


typedef struct{
	int gps_x;
	int gps_y;
	int evolution;
	int water_level;
	unsigned int water_level_threshold;
} sensor_state;


