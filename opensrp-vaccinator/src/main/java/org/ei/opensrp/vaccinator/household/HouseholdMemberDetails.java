package org.ei.opensrp.vaccinator.household;

import android.graphics.drawable.Drawable;

/**
 * Created by Safwan on 5/10/2016.
 */
public class HouseholdMemberDetails {

    public String memberId;

    public String memberName;

    public String memberRegister;

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

    public int getMemberImageId() {
        return memberImageId;
    }

    public void setMemberImageId(int memberImageId) {
        this.memberImageId = memberImageId;
    }
}
