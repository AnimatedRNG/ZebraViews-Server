package zebradev.zebraviews.server;

public enum ServerCommands {

	START ("start"), STOP ("stop");
	
	public String value;
	
	ServerCommands(String val) {
		value = val;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}
