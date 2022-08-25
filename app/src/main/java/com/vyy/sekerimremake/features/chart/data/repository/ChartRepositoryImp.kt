package com.vyy.sekerimremake.features.chart.data.repository

import com.google.firebase.firestore.CollectionReference
import com.vyy.sekerimremake.features.chart.domain.model.ChartRowModel
import com.vyy.sekerimremake.features.chart.domain.repository.ChartRepository
import com.vyy.sekerimremake.utils.Response
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ChartRepositoryImp @Inject constructor(
    private val chartRef: CollectionReference
) : ChartRepository {
    override fun getChart() = callbackFlow {
        val snapshotListener = chartRef.addSnapshotListener { snapshot, e ->
            val response = if (snapshot != null) {
                try {
                    val chartRowModels = snapshot.toObjects(ChartRowModel::class.java)
                    Response.Success(chartRowModels)
                } catch (e: Exception) {
                    Response.Error("Failed to convert Firebase snapshot to ChartModel object. ${e.message ?: e.toString()}")
                }
            } else {
                Response.Error(e?.message ?: e.toString())
            }
            trySend(response).isSuccess
        }
        awaitClose {
            snapshotListener.remove()
        }
    }

    override fun addChartRow(chartRowModel: ChartRowModel) = flow {
        try {
            emit(Response.Loading)
            if (chartRowModel.rowId != null) {
                val addition = chartRowModel.rowId?.let { chartRef.document(it).set(chartRowModel).await() }
                emit(Response.Success(addition))
            } else {
                emit(Response.Error("ChartModel document id is null."))
            }

        } catch (e: Exception) {
            emit(Response.Error(e.message ?: e.toString()))
        }
    }

    override fun deleteChartRow(id: String) = flow {
        try {
            emit(Response.Loading)
            val deletion = chartRef.document(id).delete().await()
            emit(Response.Success(deletion))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: e.toString()))
        }
    }
}