package cz.cvut.fel.jee.gourmeter.ejb;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cvut.fel.jee.gourmeter.bo.CateringFacility;
import cz.cvut.fel.jee.gourmeter.bo.OpeningHours;
import cz.cvut.fel.jee.gourmeter.bo.Tag;
import cz.cvut.fel.jee.gourmeter.bo.User;
import cz.cvut.fel.jee.gourmeter.dto.CateringFacilityDTO;
import cz.cvut.fel.jee.gourmeter.dto.OpeningHoursDTO;
import cz.cvut.fel.jee.gourmeter.dto.OpeningHoursDTO.Day;
import cz.cvut.fel.jee.gourmeter.dto.TagDTO;

@Stateless
public class FacilitySessionBean implements FacilitySessionLocal {

	private static final Logger log = LoggerFactory.getLogger(FacilitySessionBean.class);
	
	/**
	 * Conversion ratio for kilometer to coordinate degree. TODO check this
	 * value
	 */
	private static final double COORDINATE_TO_KM = 0.009132;

	/**
	 * Default search radius is 3 kilometers from actual position.
	 */
	private static final double DEFAULT_SEARCH_RADIUS_KM = 3;

	@PersistenceContext(unitName = "GourmeterPU")
	private EntityManager em;

	@EJB
	private DataSessionLocal dao;

	@Override
	public List<CateringFacility> getFacilitiesInArea(	double latitude,
														double longitude) {
		// use default circle radius
		CoordinateSearchWrapper csw = getCoordinatesWrapper(latitude,
				longitude, DEFAULT_SEARCH_RADIUS_KM);
		return dao.findFacilitiesByGPS(csw);
	}

	@Override
	public List<CateringFacility> getFacilitiesInArea(	double latitude,
														double longitude,
														double kmCircle) {

		CoordinateSearchWrapper csw = getCoordinatesWrapper(latitude,
				longitude, kmCircle);
		return dao.findFacilitiesByGPS(csw);
	}

	@Override
	public List<CateringFacility> getFacilitiesInArea(	double latitude,
														double longitude,
														double kmCircle,
														long tagId) {

		Tag tag = em.find(Tag.class, tagId);
		if (tag == null) {
			String msg = "No tag found for id : " + tagId;
			log.warn("No tag found for id : " + tagId);
			throw new IllegalArgumentException(msg);
		}

		CoordinateSearchWrapper csw = getCoordinatesWrapper(latitude,
				longitude, kmCircle);
		return dao.findFacilitiesByGPSAndTag(csw, tag);
	}

	@Override
	public void createNewFacility(CateringFacilityDTO dto, Long userId) {
		DateFormat df = new SimpleDateFormat("hh:mm");

		CateringFacility f = new CateringFacility();
		f.setCity(dto.getAddress().getCity());
		f.setCityDistrict(null); // TODO
		f.setDateOfConfirmation(null);
		f.setDescription(dto.getDescription());
		f.setHouseNumber(dto.getAddress().getHouseNumber());
		f.setLatitude(dto.getLatitude());
		f.setLongitude(dto.getLongitude());
		try {
			f.setMenuFrom(df.parse(dto.getMenu().getFrom()));
			f.setMenuTo(df.parse(dto.getMenu().getTo()));
		} catch (ParseException e) {
			log.warn(e.getMessage());
		}
		f.setMenuUrl(dto.getUrl()); // TODO
		f.setName(dto.getTitle());
		f.setStreet(dto.getAddress().getStreet());
		f.setUrl(dto.getUrl());

		f.setCategory(null); // TODO
		f.setCreator(em.find(User.class, userId));

		setOpeningHours(dto, df, f);
		setTags(dto, f);
		
		em.persist(f);
	}

	private void setTags(CateringFacilityDTO dto, CateringFacility f) {
		for (TagDTO t : dto.getTags()) {
			TypedQuery<Tag> q = em.createNamedQuery("Tag.findByName", Tag.class);
			q.setParameter("name", t.getName());
			Tag tag = null;
			try {
				tag = q.getSingleResult();
			} catch (NoResultException e) {
				tag = new Tag();
				tag.setName(t.getName());
				tag.setCategory(null); // TODO
				em.persist(tag);
			}
			
			f.getTags().add(tag);
		}
	}

	private void setOpeningHours(	CateringFacilityDTO dto,
									DateFormat df,
									CateringFacility f) {
		for (OpeningHoursDTO ohDto : dto.getOpeningHours()) {
			for (Day day : ohDto.getDays()) {
				OpeningHours oh = new OpeningHours();
				try {
					oh.setBreakFrom(df.parse(ohDto.getBreakFrom()));
					oh.setBreakTo(df.parse(ohDto.getBreakTo()));
					oh.setTimeFrom(df.parse(ohDto.getOpenFrom()));
					oh.setTimeTo(df.parse(ohDto.getOpenTo()));
				} catch (ParseException e) {
					log.warn(e.getMessage());
				}
				oh.setDayNum(day.getDayNum());
				oh.setFacility(f);
				f.getOpeningHours().add(oh);
			}
		}
	}

	private CoordinateSearchWrapper getCoordinatesWrapper(	double latitude,
															double longitude,
															double kmCircle) {
		double radius = getSearchRadius(kmCircle);
		double latMin = latitude - radius;
		double latMax = latitude + radius;
		double longMin = longitude - radius;
		double longMax = longitude + radius;
		CoordinateSearchWrapper csw = new CoordinateSearchWrapper(longMin,
				longMax, latMin, latMax);

		return csw;
	}

	private double getSearchRadius(double kilometerDistance) {
		// return search radius
		return COORDINATE_TO_KM * kilometerDistance / 2;
	}

}
