package cz.cvut.fel.jee.gourmeter.bo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Jan Šrogl
 */
@Entity
@Table(name = "catering_facility")
@NamedQueries({
		@NamedQuery(
					name = "CateringFacility.findByCoordinates",
					query = "SELECT e FROM CateringFacility e "
							+ "WHERE e.latitude < :latitudeMax AND e.latitude > :latitudeMin "
							+ "AND e.longitude < :longitudeMax AND e.longitude > :longitudeMin"),
		@NamedQuery(
					name = "CateringFacility.findByCoordinatesAndTag",
					query = "SELECT e FROM CateringFacility e "
							+ "WHERE e.latitude < :latitudeMax AND e.latitude > :latitudeMin "
							+ "AND e.longitude < :longitudeMax AND e.longitude > :longitudeMin "
							+ "AND :tag MEMBER OF e.tags") })
public class CateringFacility extends AbstractBusinessObject {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Column(nullable = false)
	private String name;

	@Column(nullable = true)
	private String description;

	@Column(nullable = false)
	private String url;

	@Column(nullable = false)
	private String city;

	@Column(nullable = true)
	private String cityDistrict;

	@Column(nullable = true)
	private String menuUrl;

	@Column(nullable = false)
	private Boolean additionConfirmed;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date dateOfConfirmation;

	@Temporal(TemporalType.TIME)
	@Column(nullable = true)
	private Date menuFrom;

	@Temporal(TemporalType.TIME)
	@Column(nullable = true)
	private Date menuTo;

	@Column(name = "latitude")
	private double latitude;

	@Column(name = "longitude")
	private double longitude;

	@ManyToOne
	private Category category;

	@ManyToOne
	private User creator;

	@OneToMany(cascade = CascadeType.ALL)
	private List<OpeningHours> openingHours;

	@ManyToMany
	@JoinTable(name = "catering_facility_tags",
				joinColumns = @JoinColumn(name = "facility_id"),
				inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private List<Tag> tags;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCityDistrict() {
		return cityDistrict;
	}

	public void setCityDistrict(String cityDistrict) {
		this.cityDistrict = cityDistrict;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public Boolean getAdditionConfirmed() {
		return additionConfirmed;
	}

	public void setAdditionConfirmed(Boolean additionConfirmed) {
		this.additionConfirmed = additionConfirmed;
	}

	public Date getDateOfConfirmation() {
		return dateOfConfirmation;
	}

	public void setDateOfConfirmation(Date dateOfConfirmation) {
		this.dateOfConfirmation = dateOfConfirmation;
	}

	public Date getMenuFrom() {
		return menuFrom;
	}

	public void setMenuFrom(Date menuFrom) {
		this.menuFrom = menuFrom;
	}

	public Date getMenuTo() {
		return menuTo;
	}

	public void setMenuTo(Date menuTo) {
		this.menuTo = menuTo;
	}

	public String getMenuUrl() {
		return menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

	public List<OpeningHours> getOpeningHours() {
		if (openingHours == null) {
			openingHours = new ArrayList<>();
		}
		return openingHours;
	}

	public void setOpeningHours(List<OpeningHours> openingHours) {
		this.openingHours = openingHours;
	}

	public List<Tag> getTags() {
		if (tags == null) {
			tags = new ArrayList<>();
		}
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
