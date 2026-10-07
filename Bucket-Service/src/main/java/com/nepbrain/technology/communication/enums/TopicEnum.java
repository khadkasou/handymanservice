package com.nepbrain.technology.communication.enums;

import lombok.Getter;

@Getter
public enum TopicEnum {
    REGISTER("Register", MessageModuleEnum.AUTH),
    ADMIN_USER_REGISTER("Admin User Register", MessageModuleEnum.AUTH),
    CUSTOMER_REGISTER("Customer Register", MessageModuleEnum.GENERAL),
    LOGIN("Login", MessageModuleEnum.AUTH),
    OTP("Otp", MessageModuleEnum.AUTH),
    FORGOT_PASSWORD("Forgot Password", MessageModuleEnum.AUTH),
    RESET_PASSWORD("Reset Password", MessageModuleEnum.AUTH),
    KYC_SUCCESS("KYC Success", MessageModuleEnum.GENERAL),
    KYC_FAILED("KYC Failure", MessageModuleEnum.GENERAL),
    KYC_PENDING("KYC Pending", MessageModuleEnum.GENERAL),
    KYC_EXPIRED("KYC Expired", MessageModuleEnum.GENERAL),
    REGISTRATION_SUCCESS("Registration Success", MessageModuleEnum.GENERAL),
    BENEFICIARY("Beneficiary", MessageModuleEnum.GENERAL),
    DEFAULT("Default", MessageModuleEnum.GENERAL),
    MFA("MFA", MessageModuleEnum.AUTH),
    RESET_PASSWORD_ADMIN("Reset Password Admin", MessageModuleEnum.AUTH),
    PARTNER_KYC("Partner Due Diligence Request ", MessageModuleEnum.GENERAL),
    PARTNER_KYC_APPROVAL("Partner Due Diligence Success", MessageModuleEnum.GENERAL),
    PARTNER_KYC_REJECTION("Partner Due Diligence Failure", MessageModuleEnum.GENERAL),
    PARTNER_KYC_EXPIRATION("Partner Due Diligence request Expired", MessageModuleEnum.GENERAL),
    TRANSACTION_SUCCESS(" Transaction success", MessageModuleEnum.GENERAL),
    TRANSACTION_FAILURE("Transaction failure", MessageModuleEnum.GENERAL),
    LOCAL_TRANSFER_SUCCESS("Local Transfer Success", MessageModuleEnum.GENERAL),
    LOCAL_TRANSFER_FAILURE("Local Transfer failure", MessageModuleEnum.GENERAL),
    MOBILE_TOPUP_SUCCESS("Mobile Topup success", MessageModuleEnum.GENERAL),
    MOBILE_TOPUP_FAILURE("Mobile Topup failure", MessageModuleEnum.GENERAL),
    TODAYS_EXCHANGE_RATE("Todays exchange rate alert", MessageModuleEnum.GENERAL),
    NEW_DEVICE_DETECTED("New Device Detected", MessageModuleEnum.AUTH),
    BULK("Bulk Message", MessageModuleEnum.GENERAL),

    // --- HRM: Public company registration ---
    COMPANY_REGISTER("Company Registration OTP", MessageModuleEnum.HRM),
    COMPANY_REGISTRATION_APPROVED("Company Registration Approved", MessageModuleEnum.HRM),
    COMPANY_REGISTRATION_REJECTED("Company Registration Rejected", MessageModuleEnum.HRM),

    // --- HRM: User account creation ---
    USER_ORGANIZATION_REGISTER("Organization User Register", MessageModuleEnum.HRM),
    USER_COMPANY_REGISTER("Company User Register", MessageModuleEnum.HRM),
    USER_BRANCH_REGISTER("Branch User Register", MessageModuleEnum.HRM),
    USER_EMPLOYEE_REGISTER("Employee User Register", MessageModuleEnum.HRM),

    // --- HRM: Leave ---
    LEAVE_REQUEST_SUBMITTED("Leave Request Submitted", MessageModuleEnum.HRM),
    LEAVE_PENDING_SUPERVISOR_APPROVAL("Leave Pending Supervisor Approval", MessageModuleEnum.HRM),
    LEAVE_SUPERVISOR_APPROVED("Leave Supervisor Approved", MessageModuleEnum.HRM),
    LEAVE_SUPERVISOR_REJECTED("Leave Supervisor Rejected", MessageModuleEnum.HRM),
    LEAVE_PENDING_HR_APPROVAL("Leave Pending HR Approval", MessageModuleEnum.HRM),
    LEAVE_APPROVED("Leave Approved", MessageModuleEnum.HRM),
    LEAVE_REJECTED("Leave Rejected", MessageModuleEnum.HRM),
    LEAVE_CANCELLED("Leave Cancelled", MessageModuleEnum.HRM),

    // --- HRM: Attendance time request ---
    ATTENDANCE_TIME_REQUEST_SUBMITTED("Attendance Time Request Submitted", MessageModuleEnum.HRM),
    ATTENDANCE_TIME_REQUEST_PENDING_APPROVAL("Attendance Time Request Pending Approval", MessageModuleEnum.HRM),
    ATTENDANCE_TIME_REQUEST_APPROVED("Attendance Time Request Approved", MessageModuleEnum.HRM),
    ATTENDANCE_TIME_REQUEST_REJECTED("Attendance Time Request Rejected", MessageModuleEnum.HRM),

    LEAVE_REQUEST("Leave Request", MessageModuleEnum.HRM),
    LEAVE_REQUEST_RECOMMEND("Leave Request Recommended", MessageModuleEnum.HRM),
    LEAVE_REQUEST_APPROVE("Leave Request Approved", MessageModuleEnum.HRM),
    LEAVE_REQUEST_REJECT("Leave Request Rejected", MessageModuleEnum.HRM),
    LEAVE_REQUEST_APPLIED("Leave Request Applied", MessageModuleEnum.HRM),
    LEAVE_REQUEST_APPLICATION_FAILED("Leave Request Application Failed", MessageModuleEnum.HRM),
    LEAVE_REQUEST_CANCELLED("Leave Request Cancelled", MessageModuleEnum.HRM),
    TIME_REQUEST("Time Request", MessageModuleEnum.HRM),
    TIME_REQUEST_RECOMMEND("Time Request Recommended", MessageModuleEnum.HRM),
    TIME_REQUEST_APPROVE("Time Request Approved", MessageModuleEnum.HRM),
    TIME_REQUEST_REJECT("Time Request Rejected", MessageModuleEnum.HRM),
    TIME_REQUEST_APPLIED("Time Request Applied", MessageModuleEnum.HRM),
    TIME_REQUEST_APPLICATION_FAILED("Time Request Application Failed", MessageModuleEnum.HRM),
    TIME_REQUEST_CANCELLED("Time Request Cancelled", MessageModuleEnum.HRM),

    // --- HRM: Compensation leave workflow ---
    COMPENSATION_LEAVE_REQUEST("Compensation Leave Request", MessageModuleEnum.HRM),
    COMPENSATION_LEAVE_REQUEST_SUBMITTED("Compensation Leave Request Submitted", MessageModuleEnum.HRM),
    COMPENSATION_LEAVE_SUPERVISOR_APPROVED("Compensation Leave Supervisor Approved", MessageModuleEnum.HRM),
    COMPENSATION_LEAVE_PENDING_HR("Compensation Leave Pending HR Approval", MessageModuleEnum.HRM),
    COMPENSATION_LEAVE_REQUEST_RECOMMEND("Compensation Leave Recommended", MessageModuleEnum.HRM),
    COMPENSATION_LEAVE_REQUEST_APPROVE("Compensation Leave Approved", MessageModuleEnum.HRM),
    COMPENSATION_LEAVE_REQUEST_REJECT("Compensation Leave Rejected", MessageModuleEnum.HRM),
    COMPENSATION_LEAVE_CREDITED("Compensation Leave Credited", MessageModuleEnum.HRM),
    COMPENSATION_LEAVE_APPLICATION_FAILED("Compensation Leave Application Failed", MessageModuleEnum.HRM),
    COMPENSATION_LEAVE_CANCELLED("Compensation Leave Cancelled", MessageModuleEnum.HRM),

    WITHDRAWAL_REQUESTED("Withdrawal Requested", MessageModuleEnum.HRM),
    WITHDRAWAL_APPROVED("Withdrawal Approved", MessageModuleEnum.HRM),
    WITHDRAWAL_REJECTED("Withdrawal Rejected", MessageModuleEnum.HRM),

    // --- HRM: Roster schedule ---
    ROSTER_SCHEDULE_PUBLISHED("Roster Schedule Published", MessageModuleEnum.HRM),

    // --- HRM: Performance reviews ---
    PERFORMANCE_REVIEW_PUBLISHED("Performance Review Published", MessageModuleEnum.HRM),
    PERFORMANCE_REVIEW_READY_FOR_REVIEWER("Performance Review Ready For Reviewer", MessageModuleEnum.HRM),
    PERFORMANCE_REVIEW_MANAGER_SUBMITTED("Performance Review Manager Submitted", MessageModuleEnum.HRM),

    // --- HRM: Leave assignment (credit workflow) ---
    LEAVE_ASSIGNMENT_AUTO_APPROVED("Leave Assignment Auto Approved", MessageModuleEnum.HRM),
    LEAVE_ASSIGNMENT_BATCH_AUTO_APPROVED("Leave Assignment Batch Auto Approved", MessageModuleEnum.HRM),

    // --- HRM: Company notices ---
    COMPANY_NOTICE("Company Notice", MessageModuleEnum.HRM),

    // --- Account deletion / privacy ---
    ACCOUNT_DELETION_REQUESTED("Account Deletion Requested", MessageModuleEnum.HRM),
    ACCOUNT_DELETION_DECISION("Account Deletion Decision", MessageModuleEnum.HRM);

    private final String value;
    private final MessageModuleEnum module;

    TopicEnum(String value, MessageModuleEnum module) {
        this.value = value;
        this.module = module;
    }
}
