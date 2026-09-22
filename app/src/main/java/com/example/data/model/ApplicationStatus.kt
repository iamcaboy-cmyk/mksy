package com.example.data.model

enum class ApplicationStatus(val code: String, val labelEn: String, val labelHi: String) {
    DRAFT("DRAFT", "Draft", "प्रारूप (ड्राफ्ट)"),
    SUBMITTED("SUBMITTED", "Submitted", "जमा किया गया"),
    UNDER_REVIEW("UNDER_REVIEW", "Under Review", "समीक्षाधीन"),
    DOCUMENT_VERIFICATION("DOCUMENT_VERIFICATION", "Document Verification", "दस्तावेज सत्यापन"),
    APPROVED("APPROVED", "Approved", "स्वीकृत"),
    REJECTED("REJECTED", "Rejected", "अस्वीकृत"),
    RETURNED_FOR_CORRECTION("RETURNED_FOR_CORRECTION", "Returned for Correction", "सुधार हेतु वापस");

    fun isEditable(): Boolean {
        return this == DRAFT || this == RETURNED_FOR_CORRECTION
    }

    companion object {
        fun fromCode(code: String): ApplicationStatus {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: DRAFT
        }
    }
}
