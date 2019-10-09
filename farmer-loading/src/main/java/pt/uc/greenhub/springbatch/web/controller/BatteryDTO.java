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
@XmlRootElement(name = "battery")
@Entity(name = "batteries")
public class BatteryDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7052919858371559142L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long sampleId;
	private String changer;
	private String health;
	private Double voltage;
	private Double temperature;
	private Integer capacity;
	private Integer changeCounter;
	private Integer currentAverage;
	private Integer currentNow;
	private Integer energyCounter;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
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

	public String getHealth() {
		return health;
	}

	public void setHealth(String health) {
		this.health = health;
	}

	public Double getVoltage() {
		return voltage;
	}

	public void setVoltage(Double voltage) {
		this.voltage = voltage;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public Integer getChangeCounter() {
		return changeCounter;
	}

	public void setChangeCounter(Integer changeCounter) {
		this.changeCounter = changeCounter;
	}

	public Integer getCurrentAverage() {
		return currentAverage;
	}

	public void setCurrentAverage(Integer currentAverage) {
		this.currentAverage = currentAverage;
	}

	public Integer getCurrentNow() {
		return currentNow;
	}

	public void setCurrentNow(Integer currentNow) {
		this.currentNow = currentNow;
	}

	public Integer getEnergyCounter() {
		return energyCounter;
	}

	public void setEnergyCounter(Integer energyCounter) {
		this.energyCounter = energyCounter;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date created) {
		this.createdAt = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	@Override
	public String toString() {
		return "BatteryDTO [id=" + id + ", sampleId=" + sampleId + ", changer=" + changer + ", health=" + health
				+ ", voltage=" + voltage + ", temperature=" + temperature + ", capacity=" + capacity
				+ ", changeCounter=" + changeCounter + ", currentAverage=" + currentAverage + ", currentNow="
				+ currentNow + ", energyCounter=" + energyCounter + ", created=" + createdAt + ", updated=" + updated
				+ "]";
	}

	public String getChanger() {
		return changer;
	}

	public void setChanger(String changer) {
		this.changer = changer;
	}

}