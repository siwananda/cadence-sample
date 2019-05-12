package com.ws.worker

import com.uber.cadence.activity.ActivityOptions
import com.uber.cadence.client.WorkflowClient
import com.uber.cadence.common.RetryOptions
import com.uber.cadence.workflow.ActivityStub
import com.uber.cadence.workflow.SignalMethod
import com.uber.cadence.workflow.Workflow
import com.uber.cadence.workflow.WorkflowMethod
import com.ws.worker.configuration.CadenceProperties
import mu.KLogging
import java.time.Duration
import java.util.*

interface IRiskWorkflow {

    @WorkflowMethod
    fun processRisk(input: String)

    @SignalMethod
    fun assignTask(user: String, wfId: String, wfRunId: String)
}


class RiskWorkflow(private val cadenceProperties: CadenceProperties) : IRiskWorkflow {
    companion object : KLogging()

    // ActivityStub to allow untypedActivityStub in Spring
    private val defaultTaskListStore: ActivityStub

    init {
        val ao = ActivityOptions.Builder()
                .setScheduleToCloseTimeout(Duration.ofSeconds(30))
                .setTaskList(cadenceProperties.taskList)
                .build()
        // Instead of this.defaultTaskListStore = Workflow.newActivityStub(RiskActivity::class.java, ao)
        this.defaultTaskListStore = Workflow.newUntypedActivityStub(ao)

    }

    override fun assignTask(user: String, wfId: String, wfRunId: String) {
        val wfInfo = Workflow.getWorkflowInfo()
        wfInfo.taskList
        val workflowClient = WorkflowClient.newInstance(cadenceProperties.domain)
        val workflowStub = workflowClient.newUntypedWorkflowStub(wfId, Optional.of(wfRunId), Optional.empty<String>())
        val execution = workflowStub.execution
        val completionClient = workflowClient.newActivityCompletionClient()
        completionClient.complete(execution, "2", user)
        logger.info { "completed assignTask" }
    }

    override fun processRisk(input: String) {
        logger.info { "received string $input" }
        val retryOptions = RetryOptions.Builder()
                .setExpiration(Duration.ofSeconds(10))
                .setInitialInterval(Duration.ofSeconds(1))
                .build()
        // Retries the whole sequence on any failure, potentially on a different host.
        Workflow.retry(retryOptions) { processRiskImpl(input) }
    }

    private fun processRiskImpl(input: String) {
        val result = defaultTaskListStore.execute("IRiskActivity::process", String::class.java, input)
        logger.info { "----------------------------------------------" }

        val stringPromise = defaultTaskListStore.executeAsync("IRiskActivity::process2", String::class.java, result)
        val res = stringPromise.get()
        logger.info { "+++++++++++++++++++ received promised $res" }
    }

}