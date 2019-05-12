package com.ws.worker.controllers

import com.ws.worker.services.WorkflowService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/workflows")
class WorkflowController(val workflowService: WorkflowService) {

    @PostMapping("")
    fun createWorkflow(@RequestBody workflow: Map<String, String> = emptyMap()): Map<String, String> {
        val workflowType = workflow.getOrDefault("workflowType", "IRiskWorkflow::processRisk")
        return workflowService.createWorkflow(workflowType)
    }
}