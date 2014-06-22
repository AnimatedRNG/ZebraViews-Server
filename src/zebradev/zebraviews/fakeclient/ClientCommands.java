package zebradev.zebraviews.fakeclient;

public enum ClientCommands {

	CONNECT ("connect"), DISCONNECT ("disconnect"), SEND ("send"), IDLE ("idle");
	
	public String value;
	
	ClientCommands(String val) {
		value = val;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}
