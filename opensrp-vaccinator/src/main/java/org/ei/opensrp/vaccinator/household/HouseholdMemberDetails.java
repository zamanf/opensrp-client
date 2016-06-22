package org.ei.opensrp.vaccinator.household;

/**
 * Created by Safwan on 5/10/2016.
 */
public class HouseholdMemberDetails {

    public String memberId;

    public String memberName;

    public String memberRegister;

    public String memberAge;

    public String memberRelationWithHousehold;

    public int memberImageId;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberRegister() {
        return memberRegister;
    }

    public void setMemberRegister(String memberRegister) {
        this.memberRegister = memberRegister;
    }

    public String getMemberAge() {
        return memberAge;
    }

    public void setMemberAge(String memberAge) {
        this.memberAge = memberAge;
    }

    public String getMemberRelationWithHousehold() {
        return memberRelationWithHousehold;
    }

    public void setMemberRelationWithHousehold(String memberRelationWithHousehold) {
        this.memberRelationWithHousehold = memberRelationWithHousehold;
    }

    public int getMemberImageId() {
        return memberImageId;
    }

    public void setMemberImageId(int memberImageId) {
        this.memberImageId = memberImageId;
    }
}
