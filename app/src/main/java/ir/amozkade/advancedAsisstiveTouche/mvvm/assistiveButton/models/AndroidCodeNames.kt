package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models

class AndroidCodeNames(val api:Int ,val  codeName:String ,val version:String) {

    companion object{
        private fun getAndroidCodeNames():List<AndroidCodeNames>{
            val list = arrayListOf<AndroidCodeNames>()
            list.add(AndroidCodeNames(16 , "Jelly Bean" , "4.1"))
            list.add(AndroidCodeNames(17 , "Jelly Bean" , "4.2"))
            list.add(AndroidCodeNames(18 , "Jelly Bean" , "4.3"))
            list.add(AndroidCodeNames(19 , "KitKat" , "4.4"))
            list.add(AndroidCodeNames(21 , "Lollipop" , "5.0"))
            list.add(AndroidCodeNames(22 , "Lollipop" , "5.1"))
            list.add(AndroidCodeNames(23 , "Marshmallow" , "6.0"))
            list.add(AndroidCodeNames(24 , "Nougat" , "7.0"))
            list.add(AndroidCodeNames(25 , "Nougat" , "7.1"))
            list.add(AndroidCodeNames(26 , "Oreo" , "8.0"))
            list.add(AndroidCodeNames(27 , "Oreo" , "8.1"))
            list.add(AndroidCodeNames(28 , "Pie" , "9.0"))
            list.add(AndroidCodeNames(29 , "Android10" , "10.0"))
            return  list
        }

        fun convertApiIntToAndroidVersionNumber(api:Int):String{
           return getAndroidCodeNames().first{ it.api == api}.version
        }
    }
}