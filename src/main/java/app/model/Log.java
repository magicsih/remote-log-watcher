package app.model;

public class Log {

	private final String senderId;
	private final String senderHost;
	private final String type;
	private final String message;

	public Log(String senderId, String senderHost, String type, String message) {
		super();
		this.senderId = senderId;
		this.senderHost = senderHost;
		this.type = type;
		this.message = message;
	}

	public String getSenderId() {
		return senderId;
	}

	public String getSenderHost() {
		return senderHost;
	}

	public String getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "Log [senderId=" + senderId + ", senderIp=" + senderHost + ", type=" + type + ", message=" + message + "]";
	}
}
