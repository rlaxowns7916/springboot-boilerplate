package com.tj.boilerplate.storage.support.transaction

import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate

@Component
class TransactionProcessor(transactionManager: PlatformTransactionManager) {
    private val executor = TransactionTemplate(transactionManager)
    private val readOnlyExecutor =
        TransactionTemplate(transactionManager).apply {
            isReadOnly = true
        }
    private val requiresNewExecutor =
        TransactionTemplate(transactionManager).apply {
            propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
        }

    fun <T> execute(block: () -> T?): T? {
        return executor.execute { block() }
    }

    fun <T> executeInReadOnly(block: () -> T?): T? {
        return readOnlyExecutor.execute { block() }
    }

    fun <T> executeInRequiresNewPropagation(block: () -> T?): T? {
        return requiresNewExecutor.execute { block() }
    }
}
