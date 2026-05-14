package com.hastashilpa.app.data
import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ── Batch Entity ──────────────────────────────────────────────
@Entity(tableName = "batches")
data class BatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val product: String,
    val poles: Int,
    val strips: Int,
    val date: String
)

// ── User Entity ───────────────────────────────────────────────
// Defined HERE only — do NOT have a separate UserEntity.kt file
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String = "",
    val passwordHash: String,
    val craft: String = "Bamboo & Cane",
    val location: String = "",
    val loginMethod: String = "EMAIL",
    val createdAt: Long = System.currentTimeMillis()
)

// ── Batch DAO ─────────────────────────────────────────────────
@Dao
interface BatchDao {
    @Query("SELECT * FROM batches ORDER BY id DESC")
    fun getAllBatches(): Flow<List<BatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: BatchEntity)

    @Delete
    suspend fun deleteBatch(batch: BatchEntity)
}

// ── User DAO ──────────────────────────────────────────────────
// Defined HERE only — do NOT have a separate UserDao.kt file
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email AND passwordHash = :hash LIMIT 1")
    suspend fun loginByEmail(email: String, hash: String): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone AND passwordHash = :hash LIMIT 1")
    suspend fun loginByPhone(phone: String, hash: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun emailExists(email: String): Int

    @Query("SELECT COUNT(*) FROM users WHERE phone = :phone AND phone != ''")
    suspend fun phoneExists(phone: String): Int

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): UserEntity?
}

// ── Database ──────────────────────────────────────────────────
@Database(
    entities = [BatchEntity::class, UserEntity::class],
    version = 2,
    exportSchema = false
)
abstract class BatchDatabase : RoomDatabase() {
    abstract fun batchDao(): BatchDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: BatchDatabase? = null

        fun getDatabase(context: Context): BatchDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BatchDatabase::class.java,
                    "hasta_shilpa_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}