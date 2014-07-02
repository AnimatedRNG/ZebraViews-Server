package zebradev.zebraviews.processor;

public class ProcessingException extends Exception {
	
	private static final long serialVersionUID = 1212914464860085325L;
	
	private String processor;
	protected Exception exception;
	
	public ProcessingException(String processor, String message, Exception exception) {
		super(message);
		this.setProcessor(processor);
		this.exception = exception;
	}
	
	@Override
	public StackTraceElement[] getStackTrace() {
		return exception.getStackTrace();
	}

	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}
}
