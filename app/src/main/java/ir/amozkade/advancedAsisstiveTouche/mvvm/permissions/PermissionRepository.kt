package ir.amozkade.advancedAsisstiveTouche.mvvm.permissions

import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.permissions.di.PermissionRetrofit
import ir.amozkade.advancedAsisstiveTouche.mvvm.permissions.utils.PermissionResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PermissionRepository @Inject constructor(private val ticketRetrofit: PermissionRetrofit) {

    fun getAllPermissions(): Flow<DataState<PermissionResponse>> = flow {
        emit(DataState.Loading)
        val permissions = ticketRetrofit.getAll()
        emit(DataState.Success(PermissionResponse.Permissions(permissions)))
    }
}