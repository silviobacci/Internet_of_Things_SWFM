#define 	ERROR 			-1
#define 	MESSAGE_SIZE		90
#define 	FIXED_STEP		10
#define 	RES_CHANGE		2
#define 	DAM_STATE_SIZE		10
#define		MIN_LEVEL_DETECTABLE	30
#define		MAX_LEVEL_DETECTABLE	500
#define 	LEVEL_SAMPLING_PERIOD	3
#define 	POS_SAMPLING_PERIOD	10

#define str(s) 		#s


static int tmp_x,tmp_y;	


typedef struct{
	int gps_x;
	int gps_y;
	int evolution;
	unsigned int water_level;
	unsigned int water_level_threshold;
	unsigned int min;
	unsigned int max;
} sensor_state;


typedef struct{
	int gpsx;
	int gpsy;
	char dam_state[7];
	unsigned int min;
	unsigned int max;
} dam_state;

