package com.example.data.model

import com.example.data.local.entity.ApplicationEntity
import com.example.data.local.entity.BankDetailsEntity
import com.example.data.local.entity.BeneficiaryEntity
import com.example.data.local.entity.DocumentEntity
import com.example.data.local.entity.StatusHistoryEntity

data class ApplicationWithDetails(
    val application: ApplicationEntity,
    val beneficiary: BeneficiaryEntity?,
    val bankDetails: BankDetailsEntity?,
    val documents: List<DocumentEntity> = emptyList(),
    val statusHistory: List<StatusHistoryEntity> = emptyList()
)
