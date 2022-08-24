package com.vyy.sekerimremake.features.chart.data.repository

import com.google.firebase.firestore.CollectionReference
import com.vyy.sekerimremake.features.chart.domain.model.ChartDayModel
import com.vyy.sekerimremake.features.chart.domain.repository.ChartRepository
import com.vyy.sekerimremake.features.chart.utils.ChartConstants.DATE
import com.vyy.sekerimremake.utils.Response
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ChartRepositoryImp @Inject constructor(
    private val chartRef: CollectionReference
): ChartRepository {
    override fun getChartFromFirestore() = callbackFlow {
        val snapshotListener = chartRef.orderBy(DATE).addSnapshotListener { snapshot, e ->
            val response = if (snapshot != null) {
                val chartModels = snapshot.toObjects(ChartDayModel::class.java)
                Response.Success(chartModels)
            } else {
                Response.Error(e?.message ?: e.toString())
            }
            trySend(response).isSuccess
        }
        awaitClose {
            snapshotListener.remove()
        }
    }
}