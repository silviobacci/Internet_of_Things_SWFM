package oneM2M.utilities;

public class OM2MConstants {
	public static final int ACP = 1;
	public static final int AE = 2;
	public static final int CONTAINER = 3;
	public static final int CONTENT_INSTANCE = 4;
	public static final int CSE_BASE = 5;
	public static final int M2M_SERVICE_SUBSCRIPTION = 11;
	public static final int REMOTE_CSE = 16;
	public static final int SUBSCRIPTION = 23;
	
	public static final int WHOLE_RESOURCE = 1; 
	public static final int MODIFIED_ATTRIBUTES = 2; 
	public static final int REFERENCE_ONLY = 3;
	
	public static final String RESOURCE_TYPE_AE = "m2m:ae";
	public static final String RESOURCE_TYPE_CONTAINER = "m2m:cnt";
	public static final String RESOURCE_TYPE_CONTENT_INSTANCE = "m2m:cin";
	public static final String RESOURCE_TYPE_REMOTE_CSE = "m2m:csr";
	public static final String RESOURCE_TYPE_SUBSCRIPTION = "m2m:sub";
	public static final String RESOURCE_TYPE_URI_LIST = "m2m:uril";
	public static final String NOTIFICATION = "m2m:sgn";
	public static final String NOTIFICATION_EVENT = "m2m:nev";
	public static final String VERIFICATION_REQUEST = "m2m:vrq";
	public static final String SUBSCRIPTION_DELETION = "m2m:sud";
	public static final String SUBSCRIPTION_REFERENCE = "m2m:sur";
	public static final String REPRESENTATION = "m2m:rep";
	
	public static final String FILTER_USAGE = "fu=1";
	public static final String FILTER_RESOURCE_TYPE = "rty=";
	public static final String ACP_ADMIN = "admin:admin";;
	
	protected static final int RESPONSE_STATUS_CODE = 265;
	protected static final int POST_SUCCESSFULL = 2001;
	protected static final int GET_SUCCESSFULL = 2000;
	protected static final int DELETE_SUCCESSFULL = 2002;
	
	protected static final int TIMEOUT = 5000;
}
