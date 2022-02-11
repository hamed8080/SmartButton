package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.clipborad

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ClipboardDao{
    @Query("SELECT  *  FROM Clipboard")
    fun getAll(): List<Clipboard>

    @Query("DELETE FROM Clipboard")
    fun deleteAll()

    @Delete
    fun delete(clipboard: Clipboard)

    @Insert
    fun insert(clipboard: Clipboard)
}