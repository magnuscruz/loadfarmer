package net.petrikainulainen.springbatch.sample;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Contains the information of a single student who has purchased the course.
 *
 * @author Petri Kainulainen
 */
@XmlRootElement(name = "sample")
@Entity(name = "samples")
public class SampleDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7052919858371559142L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private Integer deviceId;
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;
	private Integer appVersion;
	private Integer databaseVersion;
	private String batteryStage;
	private Double batteryLevel;
	private Integer memoryWired;
	private Integer memoryActive;
	private Integer memoryInactive;
	private Integer memoryFree;
	private Integer memoryUser;
	private String triggered;
	private String networkStatus;
	private Integer screenBrightness;
	private Integer screenOn;
	private String timezone;
	private String countryCode;
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(Integer appVersion) {
		this.appVersion = appVersion;
	}

	public Integer getDatabaseVersion() {
		return databaseVersion;
	}

	public void setDatabaseVersion(Integer databaseVersion) {
		this.databaseVersion = databaseVersion;
	}

	public String getBatteryStage() {
		return batteryStage;
	}

	public void setBatteryStage(String batteryStage) {
		this.batteryStage = batteryStage;
	}

	public Double getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(Double batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	public Integer getMemoryWired() {
		return memoryWired;
	}

	public void setMemoryWired(Integer memoryWired) {
		this.memoryWired = memoryWired;
	}

	public Integer getMemoryActive() {
		return memoryActive;
	}

	public void setMemoryActive(Integer memoryActive) {
		this.memoryActive = memoryActive;
	}

	public Integer getMemoryInactive() {
		return memoryInactive;
	}

	public void setMemoryInactive(Integer memoryInactive) {
		this.memoryInactive = memoryInactive;
	}

	public Integer getMemoryFree() {
		return memoryFree;
	}

	public void setMemoryFree(Integer memoryFree) {
		this.memoryFree = memoryFree;
	}

	public Integer getMemoryUser() {
		return memoryUser;
	}

	public void setMemoryUser(Integer memoryUser) {
		this.memoryUser = memoryUser;
	}

	public String getTriggered() {
		return triggered;
	}

	public void setTriggered(String triggered) {
		this.triggered = triggered;
	}

	public String getNetworkStatus() {
		return networkStatus;
	}

	public void setNetworkStatus(String networkStatus) {
		this.networkStatus = networkStatus;
	}

	public Integer getScreenBrightness() {
		return screenBrightness;
	}

	public void setScreenBrightness(Integer screenBrightness) {
		this.screenBrightness = screenBrightness;
	}

	public Integer getScreenOn() {
		return screenOn;
	}

	public void setScreenOn(Integer screenOn) {
		this.screenOn = screenOn;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	@Override
	public String toString() {
		return "SampleDTO [id=" + id + ", deviceId=" + deviceId + ", timestamp=" + timestamp + ", appVersion="
				+ appVersion + ", databaseVersion=" + databaseVersion + ", batteryStage=" + batteryStage
				+ ", batteryLevel=" + batteryLevel + ", memoryWired=" + memoryWired + ", memoryActive=" + memoryActive
				+ ", memoryInactive=" + memoryInactive + ", memoryFree=" + memoryFree + ", memoryUser=" + memoryUser
				+ ", triggered=" + triggered + ", networkStatus=" + networkStatus + ", screenBrightness="
				+ screenBrightness + ", screenOn=" + screenOn + ", timezone=" + timezone + ", countryCode="
				+ countryCode + ", created=" + created + ", updated=" + updated + "]";
	}

}
