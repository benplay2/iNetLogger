package iNetLogger;

public class LogMaster {

	private String savePath;

	public void logStartLogging(){

	}

	public void logNoInternetConnection(){

	}
	public void logHaveInternetConnection(){

	}

	public void logStopLogging(){

	}

	public void logConnectionFailed(String connectionAddress){

	}
	public void logConnectionConnected(String connectionAddress){

	}

	public void logInterfaceConnected() {
		// TODO Auto-generated method stub
		
	}

	public void logInterfaceNotConnected() {
		// TODO Auto-generated method stub
		
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
}
