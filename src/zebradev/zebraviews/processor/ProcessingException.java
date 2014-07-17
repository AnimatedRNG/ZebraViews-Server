package zebradev.zebraviews.processor;

import zebradev.zebraviews.common.Requests;

public class ProcessingException extends Exception {
	
	private static final long serialVersionUID = 1212914464860085325L;
	
	private String processor;
	protected Exception exception;
	protected Requests essentialFailed;
	
	public ProcessingException(String processor, Requests essential, String message, Exception exception) {
		super(message);
		this.setEssentialFailed(essential);
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

	public Requests getEssentialFailed() {
		return essentialFailed;
	}

	public void setEssentialFailed(Requests essentialFailed) {
		this.essentialFailed = essentialFailed;
	}
}
