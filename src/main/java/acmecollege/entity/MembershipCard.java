/********************************************************************************************************2*4*w*
 * File:  MembershipCard.java Course materials CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 * Updated by:  Group NN
 * 040923145, Liz, Quach (as from ACSIS)
 * 041075438 , Krish Patel (as from ACSIS)
 * 041082119, Emmanuel, Alabi(as from ACSIS)
 *   
 */
package acmecollege.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;



import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "membership_card")
@NamedQuery(name = MembershipCard.ALL_CARDS_QUERY_NAME, query = "SELECT mc FROM MembershipCard mc left join fetch mc.clubMembership")
@NamedQuery(name = MembershipCard.ID_CARD_QUERY_NAME, query = "SELECT mc FROM MembershipCard mc left join fetch mc.clubMembership where mc.id = :param1")
@AttributeOverride(name = "id", column = @Column(name = "card_id"))
public class MembershipCard extends PojoBase implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String ALL_CARDS_QUERY_NAME = "MembershipCard.findAll";
    public static final String ID_CARD_QUERY_NAME = "MembershipCard.findById";

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="membership_id", referencedColumnName = "membership_id")
    private ClubMembership clubMembership;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
    private Student owner;

    @Basic(optional = false)
    @Column(name="signed", columnDefinition = "BIT(1)", nullable = false)
    private byte signed;

    public MembershipCard() {
        super();
    }

    public ClubMembership getClubMembership() {
        return clubMembership;
    }

    public void setClubMembership(ClubMembership clubMembership) {
        this.clubMembership = clubMembership;
    }

    public Student getOwner() {
        return owner;
    }

    public void setOwner(Student owner) {
        this.owner = owner;
    }

    public byte getSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = (byte) (signed ? 0b0001 : 0b0000);
    }
}
