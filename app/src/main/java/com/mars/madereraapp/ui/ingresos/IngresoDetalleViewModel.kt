package com.mars.madereraapp.ui.ingresos

import com.mars.madereraapp.data.remote.IngresoApiService
import com.mars.madereraapp.data.remote.IngresoDetalleItem
import com.mars.madereraapp.ui.common.BaseDetalleViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IngresoDetalleViewModel @Inject constructor(
    private val apiService: IngresoApiService
) : BaseDetalleViewModel<IngresoDetalleItem>() {

    override suspend fun fetchDetalles(id: Int): List<IngresoDetalleItem> = apiService.getDetalle(id)
}
