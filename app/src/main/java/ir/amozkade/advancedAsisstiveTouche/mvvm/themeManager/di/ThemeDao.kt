package ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.di

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.Theme

@Dao
interface ThemeDao {
    @Query("SELECT * FROM Theme")
    suspend fun getAllTheme(): List<Theme>

    @Query("DELETE FROM THEME")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(theme: Theme)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(themes: List<Theme>)
}