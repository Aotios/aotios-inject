package com.bloder

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

@JvmInline
internal value class DebugLogger(val messageCollector: MessageCollector) {
    fun log(message: String) {
        messageCollector.report(CompilerMessageSeverity.INFO, message)
    }
}