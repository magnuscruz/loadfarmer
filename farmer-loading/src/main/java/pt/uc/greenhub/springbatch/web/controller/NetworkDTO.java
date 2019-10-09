package pt.uc.greenhub.springbatch.web.controller;

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
@XmlRootElement(name = "network")
@Entity(name = "networks")
public class NetworkDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7052919858371559142L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long sampleId;
	private String networkType;
	private String mobileNetworkType;
	private String mobileDataStatus;
	private String mobileDataActivity;
	private Integer roamingEnabled;
	private String wifiStatus;
	private String wifiSignal;
	private Integer wifiSpeed;
	private String wifiApStatus;
	private String networkOperator;
	private String simOperator;
	private String mcc;
	private String mnc;
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	public Long getSampleId() {
		return sampleId;
	}

	public void setSampleId(Long sampleId) {
		this.sampleId = sampleId;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getMobileNetworkType() {
		return mobileNetworkType;
	}

	public void setMobileNetworkType(String mobileNetworkType) {
		this.mobileNetworkType = mobileNetworkType;
	}

	public String getMobileDataStatus() {
		return mobileDataStatus;
	}

	public void setMobileDataStatus(String mobileDataStatus) {
		this.mobileDataStatus = mobileDataStatus;
	}

	public String getMobileDataActivity() {
		return mobileDataActivity;
	}

	public void setMobileDataActivity(String mobileDataActivity) {
		this.mobileDataActivity = mobileDataActivity;
	}

	public Integer getRoamingEnabled() {
		return roamingEnabled;
	}

	public void setRoamingEnabled(Integer roamingEnabled) {
		this.roamingEnabled = roamingEnabled;
	}

	public String getWifiStatus() {
		return wifiStatus;
	}

	public void setWifiStatus(String wifiStatus) {
		this.wifiStatus = wifiStatus;
	}

	public String getWifiSignal() {
		return wifiSignal;
	}

	public void setWifiSignal(String wifiSignal) {
		this.wifiSignal = wifiSignal;
	}

	public Integer getWifiSpeed() {
		return wifiSpeed;
	}

	public void setWifiSpeed(Integer wifiSpeed) {
		this.wifiSpeed = wifiSpeed;
	}

	public String getWifiApStatus() {
		return wifiApStatus;
	}

	public void setWifiApStatus(String wifiApStatus) {
		this.wifiApStatus = wifiApStatus;
	}

	public String getNetworkOperator() {
		return networkOperator;
	}

	public void setNetworkOperator(String networkOperator) {
		this.networkOperator = networkOperator;
	}

	public String getSimOperator() {
		return simOperator;
	}

	public void setSimOperator(String simOperator) {
		this.simOperator = simOperator;
	}

	public String getMcc() {
		return mcc;
	}

	public void setMcc(String mcc) {
		this.mcc = mcc;
	}

	public String getMnc() {
		return mnc;
	}

	public void setMnc(String mnc) {
		this.mnc = mnc;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date createdAt) {
		this.created = createdAt;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	@Override
	public String toString() {
		return "NetworkDTO [id=" + id + ", sampleId=" + sampleId + ", networkType=" + networkType
				+ ", mobileNetworkType=" + mobileNetworkType + ", mobileDataStatus=" + mobileDataStatus
				+ ", mobileDataActivity=" + mobileDataActivity + ", roamingEnabled=" + roamingEnabled + ", wifiStatus="
				+ wifiStatus + ", wifiSignal=" + wifiSignal + ", wifiSpeed=" + wifiSpeed + ", wifiApStatus="
				+ wifiApStatus + ", networkOperator=" + networkOperator + ", simOperator=" + simOperator + ", mcc="
				+ mcc + ", mnc=" + mnc + ", createdAt=" + created + ", updated=" + updated + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}