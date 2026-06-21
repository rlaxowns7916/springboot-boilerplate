package com.tj.boilerplate.storage.rdb.support.transaction

import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate

@Component
class TransactionProcessor(
    transactionManager: PlatformTransactionManager,
) {
    private val executor = TransactionTemplate(transactionManager)
    private val readOnlyExecutor =
        TransactionTemplate(transactionManager).apply {
            isReadOnly = true
        }
    private val requiresNewExecutor =
        TransactionTemplate(transactionManager).apply {
            propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
        }

    fun <T> execute(block: () -> T?): T? = executor.execute { block() }

    fun <T> executeInReadOnly(block: () -> T?): T? = readOnlyExecutor.execute { block() }

    fun <T> executeInRequiresNewPropagation(block: () -> T?): T? = requiresNewExecutor.execute { block() }
}
