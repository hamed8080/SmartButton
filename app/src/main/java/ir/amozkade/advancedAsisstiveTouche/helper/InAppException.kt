package ir.amozkade.advancedAsisstiveTouche.helper

class InAppException(val title:String, override val message:String, val imageId:Int?): Exception() {
}