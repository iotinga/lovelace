package it.netgrid.lovelace.model;

public class ErrorMessage {

	private String type;
	private String reason;
	private String message;
	
	public ErrorMessage() {}
	
	public ErrorMessage(String type, String reason, String message) {
		this.type = type;
		this.reason = reason;
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
}
