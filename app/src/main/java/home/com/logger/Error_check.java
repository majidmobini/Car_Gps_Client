package home.com.logger;



public  class Error_check 
{
	public static void error_check(Exception ex,String location)
	{
//		if(ex.getMessage().equals("NullPointerException" ))
		{
			Error_log.InsertLog(ex, ex.getStackTrace(),ex.getStackTrace()[0].getClassName()+"-"+ex.getStackTrace()[0].getMethodName()+"\n");

		}
	}
}
