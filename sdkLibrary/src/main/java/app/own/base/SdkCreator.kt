package app.own.base

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object SdkCreator {
    internal val executorService: ExecutorService by lazy { Executors.newFixedThreadPool(1) }
}