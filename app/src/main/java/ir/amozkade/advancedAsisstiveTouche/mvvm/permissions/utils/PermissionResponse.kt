package ir.amozkade.advancedAsisstiveTouche.mvvm.permissions.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.permissions.models.Permission

sealed class PermissionResponse{
    data class Permissions(val permissions: List<Permission>): PermissionResponse()
}
