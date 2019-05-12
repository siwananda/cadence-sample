package com.ws.worker.configuration

import com.uber.cadence.DomainAlreadyExistsError
import com.uber.cadence.RegisterDomainRequest
import com.uber.cadence.serviceclient.WorkflowServiceTChannel
import mu.KLogging
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun registerDomain(cadenceProperties: CadenceProperties) {
    val cadenceService = WorkflowServiceTChannel()
    val request = RegisterDomainRequest()
    request.setDescription("Java Samples")
    request.isEmitMetric = false
    request.setName(cadenceProperties.domain)
    request.setWorkflowExecutionRetentionPeriodInDays(cadenceProperties.retentionPeriodInDays)
    try {
        cadenceService.RegisterDomain(request)
        logger.info("Successfully registered domain \"${cadenceProperties.domain}\" " +
                "with retentionDays=${cadenceProperties.retentionPeriodInDays}")
    } catch (e: DomainAlreadyExistsError) {
        logger.info("Domain \"${cadenceProperties.domain}\" is already registered")
    }
}