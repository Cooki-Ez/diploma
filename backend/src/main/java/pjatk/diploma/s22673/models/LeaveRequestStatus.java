package pjatk.diploma.s22673.models;

import lombok.Getter;

@Getter
public enum LeaveRequestStatus {

    APPROVED_S("Auto-approved: Sufficient points and no project constraints"),
    APPROVED("Manually approved"),
    DECLINED_S("Auto-declined: Insufficient leave points"),
    MANUAL("Manual review required: Leave without points or project constraints"),
    PENDING("Pending evaluation"),
    DECLINED("Declined"),
    CANCELLED("Cancelled");

    private final String systemComment;

    LeaveRequestStatus(String systemComment) {
        this.systemComment = systemComment;
    }

}

