package com.ws.worker.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties("cadence.worker")
class CadenceProperties {
    lateinit var domain: String
    lateinit var taskList: String
    var retentionPeriodInDays: Int = 1
}
