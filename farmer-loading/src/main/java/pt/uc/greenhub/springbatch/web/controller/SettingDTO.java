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
@XmlRootElement(name = "setting")
@Entity(name = "settings")
public class SettingDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7052919858371559142L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long sampleId;
	private Integer bluetoothEnabled;
	private Integer locationEnabled;
	private Integer powerSaverEnabled;
	private Integer flashLightEnabled;
	private Integer nfcEnabled;
	private Integer unknownSource;
	private Integer developerMode;
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

	public Long getSampleId() {
		return sampleId;
	}

	public void setSampleId(Long sampleId) {
		this.sampleId = sampleId;
	}

	public Integer getBluetoothEnabled() {
		return bluetoothEnabled;
	}

	public void setBluetoothEnabled(Integer bluetoothEnabled) {
		this.bluetoothEnabled = bluetoothEnabled;
	}

	public Integer getLocationEnabled() {
		return locationEnabled;
	}

	public void setLocationEnabled(Integer locationEnabled) {
		this.locationEnabled = locationEnabled;
	}

	public Integer getPowerSaverEnabled() {
		return powerSaverEnabled;
	}

	public void setPowerSaverEnabled(Integer powerSaverEnabled) {
		this.powerSaverEnabled = powerSaverEnabled;
	}

	public Integer getFlashLightEnabled() {
		return flashLightEnabled;
	}

	public void setFlashLightEnabled(Integer flashLightEnabled) {
		this.flashLightEnabled = flashLightEnabled;
	}

	public Integer getNfcEnabled() {
		return nfcEnabled;
	}

	public void setNfcEnabled(Integer nfcEnabled) {
		this.nfcEnabled = nfcEnabled;
	}

	public Integer getUnknownSource() {
		return unknownSource;
	}

	public void setUnknownSource(Integer unknownSource) {
		this.unknownSource = unknownSource;
	}

	public Integer getDeveloperMode() {
		return developerMode;
	}

	public void setDeveloperMode(Integer developrMode) {
		this.developerMode = developrMode;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public String toString() {
		return "SettingDTO [id=" + id + ", sampleId=" + sampleId + ", bluetoothEnabled=" + bluetoothEnabled
				+ ", locationEnabled=" + locationEnabled + ", powerSaverEnabled=" + powerSaverEnabled
				+ ", flashLightEnabled=" + flashLightEnabled + ", nfcEnabled=" + nfcEnabled + ", unknownSource="
				+ unknownSource + ", developerMode=" + developerMode + ", created=" + created + ", updated=" + updated
				+ "]";
	}

}