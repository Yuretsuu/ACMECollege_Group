/********************************************************************************************************2*4*w*
 * File:  PojoListener.java Course materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 *
 * Updated by:  Group NN
 * 040923145, Liz, Quach (as from ACSIS)
 * 041075438 , Krish Patel (as from ACSIS)
 * 041082119, Emmanuel, Alabi(as from ACSIS)
 *
 */
package acmecollege.entity;

import java.time.LocalDateTime;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@SuppressWarnings("unused")

public class PojoListener {

	@PrePersist
	public void setCreatedOnDate(PojoBase pojoBase) {
		LocalDateTime now = LocalDateTime.now();
		pojoBase.setCreated(now);
		pojoBase.setUpdated(now);
	}

	@PreUpdate
	public void setUpdatedDate(PojoBase pojoBase) {
		pojoBase.setUpdated(LocalDateTime.now());
	}

}
