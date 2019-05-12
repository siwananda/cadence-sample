package com.ws.worker

import com.uber.cadence.activity.Activity
import mu.KLogging

interface IRiskActivity{

    fun process(input: String): String
    fun process2(input: String)

}

class RiskActivity: IRiskActivity{
    companion object : KLogging()

    override fun process(input: String): String {
        logger.info { "------------------------------------------------------RiskActivity with input $input" }
        logger.info { "activityTask -> ${Activity.getTask()}" }
        return "after $input"
    }

    override fun process2(input: String) {
        logger.info { "------------------------------------------------------RiskActivity with input $input"}
        logger.info { "activityTask -> ${Activity.getTask()}" }
        Activity.doNotCompleteOnReturn()
    }

}