package com.vyy.sekerimremake.features.chart.data.repository

import com.google.firebase.firestore.CollectionReference
import com.vyy.sekerimremake.features.chart.domain.model.ChartDayModel
import com.vyy.sekerimremake.features.chart.domain.repository.AddChartResponse
import com.vyy.sekerimremake.features.chart.domain.repository.ChartRepository
import com.vyy.sekerimremake.features.chart.domain.repository.DeleteChartResponse
import com.vyy.sekerimremake.utils.Response
import kotlinx.coroutines.channels.awaitClose
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
                    val chartDayModels = snapshot.toObjects(ChartDayModel::class.java)
                    Response.Success(chartDayModels)
                } catch (e: Exception) {
                    Response.Error("Failed to convert Firebase snapshot to ChartModel object. ${e.message ?: e.toString()}")
                }
            } else {
                Response.Error(e?.message ?: e.toString())
            }
            trySend(response)
        }
        awaitClose {
            snapshotListener.remove()
        }
    }

    override suspend fun addChartRow(chartDayModel: ChartDayModel): AddChartResponse {
        return try {
            val id = chartDayModel.id
            if (id != null) {
                chartRef.document(id).set(chartDayModel).await()
                Response.Success(true)
            } else {
                Response.Error("ChartModel document id is null.")
            }
        } catch (e: Exception) {
            Response.Error(e.message ?: e.toString())
        }
    }

    override suspend fun deleteChartRow(id: String): DeleteChartResponse {
        return try {
            chartRef.document(id).delete().await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Error(e.message ?: e.toString())
        }
    }
}