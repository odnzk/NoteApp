package com.noteapp.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.entity.NoteEntity
import com.example.data.tuples.NoteWithCategoriesTuple
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao : BaseDao<NoteEntity> {

    @Transaction
    @Query("SELECT * FROM notes")
    fun getAll(): Flow<List<NoteWithCategoriesTuple>>

    @Transaction
    @Query("SELECT * FROM notes WHERE note_id = :noteId")
    suspend fun getById(noteId: Long): NoteWithCategoriesTuple?

    @Transaction
    @Query(
        "SELECT * FROM note_categories_table" +
                " JOIN notes ON notes.note_id = note_categories_table.note_id" +
                " AND category_id in (:categoryIds) AND notes.title LIKE '%' || :noteTitle || '%'"
    )
    fun getByCategoryId(
        categoryIds: Set<Long>,
        noteTitle: String
    ): Flow<List<NoteWithCategoriesTuple>>

    @Transaction
    @Query("SELECT * FROM notes WHERE title LIKE '%' || :noteTitle || '%'")
    fun getByTitle(noteTitle: String): Flow<List<NoteWithCategoriesTuple>>

    @Query("DELETE FROM notes WHERE note_id = :noteId")
    suspend fun deleteById(noteId: Long)

    @Transaction
    @Query("SELECT * FROM notes WHERE note_id = :id")
    fun getFlowById(id: Long): Flow<NoteWithCategoriesTuple?>

    @Query("DELETE FROM notes")
    suspend fun deleteAll()

    @Query("DELETE FROM note_categories_table WHERE note_id = :noteId AND category_id =:categoryId")
    suspend fun removeNoteCategory(noteId: Long, categoryId: Long)

    @Query("INSERT INTO note_categories_table VALUES (:noteId, :categoryId)")
    suspend fun insertNoteCategory(noteId: Long, categoryId: Long)

}
