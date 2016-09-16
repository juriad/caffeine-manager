package cz.artique.test.caffeineManager.service;

import org.joda.time.DateTime;

public class HistoryEntry {

	private final String userLogin;
	private final int userId;

	private final String machineName;
	private final int machineId;

	private final DateTime timestamp;

	public HistoryEntry(String userLogin, int userId, String machineName, int machineId, DateTime timestamp) {
		this.userLogin = userLogin;
		this.userId = userId;
		this.machineName = machineName;
		this.machineId = machineId;
		this.timestamp = timestamp;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public int getUserId() {
		return userId;
	}

	public String getMachineName() {
		return machineName;
	}

	public int getMachineId() {
		return machineId;
	}

	public DateTime getTimestamp() {
		return timestamp;
	}

}
