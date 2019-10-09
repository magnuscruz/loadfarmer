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
 * Contains the information of a single cpu provider who has purchased the
 * course.
 *
 * @author Petri Kainulainen
 */
@XmlRootElement(name = "cpu")
@Entity(name = "cpus")
public class CpuDTO implements Serializable {

	private static final long serialVersionUID = 7511351305895678516L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long sampleId;
	private Double usage;
	private Integer upTime;
	private Integer sleepTime;
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

	public Double getUsage() {
		return usage;
	}

	public void setUsage(Double usage) {
		this.usage = usage;
	}

	public Integer getUpTime() {
		return upTime;
	}

	public void setUpTime(Integer upTime) {
		this.upTime = upTime;
	}

	public Integer getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(Integer sleepTime) {
		this.sleepTime = sleepTime;
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
		return "CpuDTO [id=" + id + ", sampleId=" + sampleId + ", usage=" + usage + ", upTime=" + upTime
				+ ", sleepTime=" + sleepTime + ", created=" + created + ", updated=" + updated + "]";
	}

	public Long getSampleId() {
		return sampleId;
	}

	public void setSampleId(Long sampleId) {
		this.sampleId = sampleId;
	}
}