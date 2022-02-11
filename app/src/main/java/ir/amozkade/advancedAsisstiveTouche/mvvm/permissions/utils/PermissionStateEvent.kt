package ir.amozkade.advancedAsisstiveTouche.mvvm.permissions.utils


sealed class PermissionStateEvent {
    object GetAllPermissions : PermissionStateEvent()
}