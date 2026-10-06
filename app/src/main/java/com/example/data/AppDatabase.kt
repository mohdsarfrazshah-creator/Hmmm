package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "transfer_records")
data class TransferRecordEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val timestamp: Long = System.currentTimeMillis(),
  val peerDeviceName: String,
  val peerDeviceType: String,
  val transferMode: String, // "SEND" or "RECEIVE"
  val totalBytes: Long,
  val totalFiles: Int,
  val durationSeconds: Long,
  val avgSpeedMbps: Double,
  val connectionMethod: String,
  val status: String, // "COMPLETED", "FAILED", "CANCELLED"
  val isVerified: Boolean = true
)

@Dao
interface TransferDao {
  @Query("SELECT * FROM transfer_records ORDER BY timestamp DESC")
  fun getAllTransfers(): Flow<List<TransferRecordEntity>>

  @Query("SELECT * FROM transfer_records ORDER BY timestamp DESC LIMIT 5")
  fun getRecentTransfers(): Flow<List<TransferRecordEntity>>

  @Query("SELECT COUNT(*) FROM transfer_records WHERE status = 'COMPLETED'")
  fun getCompletedCount(): Flow<Int>

  @Query("SELECT SUM(totalBytes) FROM transfer_records WHERE status = 'COMPLETED'")
  fun getTotalTransferredBytes(): Flow<Long?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransfer(record: TransferRecordEntity): Long

  @Query("DELETE FROM transfer_records WHERE id = :id")
  suspend fun deleteTransfer(id: Long)

  @Query("DELETE FROM transfer_records")
  suspend fun clearHistory()
}

@Database(entities = [TransferRecordEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun transferDao(): TransferDao
}
