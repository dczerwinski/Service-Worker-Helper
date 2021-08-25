package com.wat.serviceworkerhelper.model.daos

import androidx.room.*
import com.wat.serviceworkerhelper.model.entities.Guide
import kotlinx.coroutines.flow.Flow

@Dao
interface GuideEntityDao {

    /**
     * @return Zwraca listę wszystkich poradników
     */
    @Query("SELECT * FROM guides_table ORDER BY title ASC")
    fun getAllGuides(): Flow<List<Guide>>

    /**
     * @param status Status poradnika
     * @return Zwraca listę poradników które mają taki sam status jak podany w parametrze
     */
    @Query("SELECT * FROM guides_table WHERE guideStatus LIKE :status ORDER BY title ASC")
    fun getAllGuidesByStatus(status: Guide.Status): Flow<List<Guide>>

    /**
     * @param creatorUID Identyfikator użytkownika
     * @return Zwarca listę poradników, których autrem jest użytkownik o identyfikatorze podanym w parametrze
     */
    @Query("SELECT * FROM guides_table WHERE creatorUID LIKE :creatorUID ORDER BY title ASC")
    fun getAllGuidesByCreator(creatorUID: String): Flow<List<Guide>>

    /**
     * @param guide Dodaje do bazy danych poradnik przekazany w parametrze
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(guide: Guide)

    /**
     * @param guides Dodaje do bazy danych listę poradników przekazanych w parametrze
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(guides: List<Guide>)

    /**
     * @param guide Aktualizuje poradnik podany w parametrze
     */
    @Update(entity = Guide::class)
    suspend fun update(guide: Guide)

    /**
     * Usuwa wszystkie poradniki z bazy danych
     */
    @Query("DELETE FROM guides_table")
    suspend fun deleteAll()

    /**
     * @param uid Usuwa poradnik o identyfikatorze podanym w parametrze
     */
    @Query("DELETE FROM guides_table WHERE uid LIKE :uid")
    suspend fun delete(uid: String)
}