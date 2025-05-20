package com.example.frontproject.data.repository

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.frontproject.domain.util.ResourceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId

interface HealthConnectRepository {
    suspend fun getTodaySteps(): ResourceState<Long>
    suspend fun checkPermissions(): Boolean
    fun getSdkStatus(): HealthConnectAvailability
    val permissions: Set<String>
}

enum class HealthConnectAvailability {
    AVAILABLE,
    NOT_INSTALLED,
    NOT_SUPPORTED
}

class HealthConnectRepositoryImpl(private val context: Context) : HealthConnectRepository {

    private val healthConnectClient: HealthConnectClient? by lazy {
        if (getSdkStatus() == HealthConnectAvailability.AVAILABLE) {
            HealthConnectClient.getOrCreate(context)
        } else {
            null
        }
    }

    override val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class)
    )

    override fun getSdkStatus(): HealthConnectAvailability {
        return when (HealthConnectClient.getSdkStatus(context)) {
            HealthConnectClient.SDK_UNAVAILABLE -> HealthConnectAvailability.NOT_SUPPORTED
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> HealthConnectAvailability.NOT_INSTALLED
            HealthConnectClient.SDK_AVAILABLE -> HealthConnectAvailability.AVAILABLE
            else -> HealthConnectAvailability.NOT_SUPPORTED
        }
    }
    override suspend fun checkPermissions(): Boolean {
        healthConnectClient ?: return false // Клиент недоступен
        return healthConnectClient!!.permissionController.getGrantedPermissions().containsAll(permissions)
    }

    override suspend fun getTodaySteps(): ResourceState<Long> = withContext(Dispatchers.IO) {
        if (healthConnectClient == null) {
            return@withContext ResourceState.Error("Health Connect не установлен или недоступен.")
        }
        if (!checkPermissions()) {
            return@withContext ResourceState.Error("Нет разрешений на чтение шагов. Перейдите в настройки Health Connect.")
        }

        try {
            val today = LocalDate.now()
            val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant()
            val endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().minusNanos(1)

            val request = ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
            )
            val response = healthConnectClient!!.readRecords(request)
            val totalSteps = response.records.sumOf { it.count }
            ResourceState.Success(totalSteps)
        } catch (e: Exception) {
            ResourceState.Error("Ошибка при чтении шагов: ${e.message}")
        }
    }
}