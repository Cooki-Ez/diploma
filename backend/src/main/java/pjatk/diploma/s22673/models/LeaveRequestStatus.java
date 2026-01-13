package pjatk.diploma.s22673.models;

public enum LeaveRequestStatus {
    PENDING,
    APPROVED,
    APPROVED_S, // approved by system
    CANCELLED,
    DECLINED,
    DECLINED_S, // declined by system
    MANUAL // requires manual review
}
