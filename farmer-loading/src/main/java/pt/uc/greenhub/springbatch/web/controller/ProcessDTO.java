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
@XmlRootElement(name = "process")
@Entity(name = "processes")
public class ProcessDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7052919858371559142L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long sampleId;
	private Long processId;
	private String name;
	private String applicationLabel;
	private String isSystemApp;
	private String importance;
	private String versionName;
	private Integer versionCode;
	private String installationPackage;
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

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApplicationLabel() {
		return applicationLabel;
	}

	public void setApplicationLabel(String applicationLabel) {
		this.applicationLabel = applicationLabel;
	}

	public String getIsSystemApp() {
		return isSystemApp;
	}

	public void setIsSystemApp(String isSystemApp) {
		this.isSystemApp = isSystemApp;
	}

	public String getImportance() {
		return importance;
	}

	public void setImportance(String importance) {
		this.importance = importance;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public Integer getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(Integer versionCode) {
		this.versionCode = versionCode;
	}

	public String getInstallationPackage() {
		return installationPackage;
	}

	public void setInstallationPackage(String installationPackage) {
		this.installationPackage = installationPackage;
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
		return "ProcessDTO [id=" + id + ", sampleId=" + sampleId + ", processId=" + processId + ", name=" + name
				+ ", applicationLabel=" + applicationLabel + ", isSystemApp=" + isSystemApp + ", importance="
				+ importance + ", versionName=" + versionName + ", versionCode=" + versionCode
				+ ", installationPackage=" + installationPackage + ", created=" + created + ", updated=" + updated
				+ "]";
	}

}
