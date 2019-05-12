package com.ws.worker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CadenceWorkerApplication

fun main(args: Array<String>) {
    runApplication<CadenceWorkerApplication>(*args)
}