package com.ws.worker.services

import com.uber.cadence.client.WorkflowClient
import com.uber.cadence.client.WorkflowOptions
import com.ws.worker.WorkflowRegister
import com.ws.worker.configuration.CadenceProperties
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class WorkflowService(val cadenceProperties: CadenceProperties){

    fun createWorkflow(workflowType: String): Map<String, String>{
        val workflowClient = WorkflowClient.newInstance(cadenceProperties.domain)

        //Use stub instead of typed
        //val workflow = workflowClient.newWorkflowStub(RiskWorkflow::class.java)
        val workflowStub = workflowClient.newUntypedWorkflowStub(workflowType,
                WorkflowOptions.Builder()
                        .setExecutionStartToCloseTimeout(Duration.ofHours(30))
                        .setTaskList(cadenceProperties.taskList)
                        .build()

        )


        WorkflowRegister.logger.info { "Executing RiskWorkflow" }
        val workflowExecution = workflowStub.start("TASK_START")

        WorkflowRegister.logger.info {
            "Started periodic workflow with workflowId=\" ${workflowExecution.getWorkflowId()}\" " +
                    "and runId=\"${workflowExecution.getRunId()}\""
        }

        return mapOf(Pair("WorkflowId", workflowExecution.getWorkflowId()), Pair("WorkflowRunId", workflowExecution.getRunId()))
    }
}