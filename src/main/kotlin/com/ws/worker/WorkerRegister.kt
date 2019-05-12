package com.ws.worker

import com.uber.cadence.worker.Worker
import com.ws.worker.configuration.CadenceProperties
import com.ws.worker.configuration.registerDomain
import mu.KLogging
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class WorkerRegister(val cadenceProperties: CadenceProperties) {
    companion object : KLogging()

    fun initWorker() {
        val factory = Worker.Factory(cadenceProperties.domain)
        val workerForCommonTaskList = factory.newWorker(cadenceProperties.taskList)

        workerForCommonTaskList.addWorkflowImplementationFactory(IRiskWorkflow::class.java) { RiskWorkflow(cadenceProperties) }

        val activityHandler = RiskActivity()
        workerForCommonTaskList.registerActivitiesImplementations(activityHandler)

        factory.start()
        logger.info { "Worker started" }
    }

    @PostConstruct
    fun init() {
        registerDomain(cadenceProperties)
        initWorker()
    }
}