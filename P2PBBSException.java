public class P2PBBSException extends Exception {
	  public static final String GETTCPSOCKETFAILED="Get TCP Socket failed";
		public static final String GETUDPSOCKETFAILED="Get UDP Socket failed";
		public static final String TCPTRANSERROR="TCP transmission error";
		public static final String DBERROR="DataBase error ";
	  public P2PBBSException(String message){
		    super(message);
	  }
}
