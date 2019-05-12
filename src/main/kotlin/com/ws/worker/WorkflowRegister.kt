package com.ws.worker

import com.uber.cadence.client.WorkflowClient
import com.uber.cadence.client.WorkflowOptions
import com.ws.worker.configuration.CadenceProperties
import com.ws.worker.configuration.registerDomain
import mu.KLogging
import org.springframework.stereotype.Service
import java.time.Duration
import javax.annotation.PostConstruct

@Service
class WorkflowRegister(val cadenceProperties: CadenceProperties) {
    companion object : KLogging()

    fun initWorkflow() {
        val workflowClient = WorkflowClient.newInstance(cadenceProperties.domain)

        //Use stub instead of typed
        //val workflow = workflowClient.newWorkflowStub(RiskWorkflow::class.java)
        val workflowStub = workflowClient.newUntypedWorkflowStub("IRiskWorkflow::processRisk",
                WorkflowOptions.Builder()
                        .setExecutionStartToCloseTimeout(Duration.ofHours(30))
                        .setTaskList(cadenceProperties.taskList)
                        .build()

        )


        logger.info { "Executing RiskWorkflow" }
        val workflowExecution = workflowStub.start("TASK_START")

        try {
            //Temporarily sleeping to allow sometime before starting to send signal
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        workflowStub.signal("IRiskWorkflow::assignTask", "assignSignal", workflowExecution.getWorkflowId(), workflowExecution.getRunId())

        logger.info {
            "Started periodic workflow with workflowId=\" ${workflowExecution.getWorkflowId()}\" " +
                    "and runId=\"${workflowExecution.getRunId()}\""
        }

    }

    @PostConstruct
    fun init() {
        registerDomain(cadenceProperties)
        initWorkflow()
    }
}

