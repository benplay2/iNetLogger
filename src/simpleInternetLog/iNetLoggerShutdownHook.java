package simpleInternetLog;
/*
 * 
 * Created by Ben Brust 2017
 */
public class iNetLoggerShutdownHook extends Thread {

	private ConnectionMaster master;

	public iNetLoggerShutdownHook(ConnectionMaster master){
		this.setMaster(master);
	}

	@Override
	public void run() {
		//System.out.println("=== my shutdown hook activated");

		this.getMaster().endProgram();

	}

	private void setMaster(ConnectionMaster master){
		this.master = master;
	}

	private ConnectionMaster getMaster(){
		return this.master;
	}

}