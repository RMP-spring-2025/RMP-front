package com.example.frontproject.api

import android.util.Log
import com.example.frontproject.data.model.RequestIdResponse
import com.example.frontproject.domain.util.ResourceState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Response
import java.io.IOException

private const val POLLING_INTERVAL_MS = 500L // Интервал опроса (0.5 секунды)
private const val POLLING_TIMEOUT_MS = 10000L // Таймаут опроса (10 секунд)

class ApiRequestExecutor(
    val apiService: ApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    /**
     * Выполняет API-вызов, обрабатывая тяжелые запросы с опросом.
     *
     * @param T Тип ожидаемых данных в ФИНАЛЬНОМ ответе ПОСЛЕ ОПРОСА (например, StatsResponse).
     * @param initialCall Лямбда-функция для выполнения первоначального запроса к API (ожидает Response<RequestIdResponse>).
     * @param pollingCall Лямбда-функция для выполнения запроса опроса результата по requestId (ожидает Response<T>).
     * @return ResourceState<T> Состояние результата: Success, Error или Loading.
     */
    suspend fun <T : Any> executeHeavyRequest(
        initialCall: suspend () -> Response<RequestIdResponse>,
        pollingCall: suspend (String) -> Response<T>
    ): ResourceState<T> = withContext(ioDispatcher) {
        try {
            val initialResponse = initialCall()

            if (!initialResponse.isSuccessful) {
                return@withContext ResourceState.Error("API Error (Initial): ${initialResponse.code()} ${initialResponse.message()}")
            }

            val requestId = initialResponse.body()?.requestId
            if (requestId.isNullOrBlank()) {
                val errorBody = initialResponse.errorBody()?.string() ?: initialResponse.body().toString()
                Log.e("ApiRequestExecutor", "Empty or invalid requestId received. Body: $errorBody")
                return@withContext ResourceState.Error("Empty or invalid requestId received")
            }

            println("Received requestId: $requestId. Starting polling...")
            val result = pollForResult(requestId, pollingCall)
            println("Polling finished for requestId: $requestId with result: $result")
            return@withContext result

        } catch (e: IOException) {
            println("Network Error (Initial): ${e.message}")
            return@withContext ResourceState.Error("Network Error: ${e.message ?: "Unknown I/O error"}")
        } catch (e: Exception) {
            println("Execution Error (Initial or Parsing): ${e.message}")
            Log.e("ApiRequestExecutor", "Execution Error (Initial or Parsing)", e) // Логируем стек трейс
            return@withContext ResourceState.Error("Execution Error: ${e.message ?: "An unexpected error occurred"}")
        }
    }

    /**
     * Выполняет опрос результата тяжелого запроса.
     * Тип T соответствует ожидаемому типу ответа от pollingCall (например, StatsResponse).
     */
    private suspend fun <T : Any> pollForResult(
        requestId: String,
        pollingCall: suspend (String) -> Response<T>
    ): ResourceState<T> {
        val result = withTimeoutOrNull(POLLING_TIMEOUT_MS) {
            while (true) {
                try {
                    println("Polling for requestId: $requestId...")
                    val pollingResponse = pollingCall(requestId)

                    if (!pollingResponse.isSuccessful) {
                        println("Polling Status: ${pollingResponse.code()} for requestId: $requestId")
                        if (pollingResponse.code() == 404) {
                            println("Polling... Result not yet available (404) for requestId: $requestId")
                            delay(POLLING_INTERVAL_MS)
                            continue
                        }
                        val errorBody = pollingResponse.errorBody()?.string()
                        Log.e("ApiRequestExecutor", "Polling Error: ${pollingResponse.code()} ${pollingResponse.message()}. Body: $errorBody")
                        return@withTimeoutOrNull ResourceState.Error("Polling Error: ${pollingResponse.code()} ${pollingResponse.message()}")
                    }

                    val pollingApiResponse = pollingResponse.body()
                    if (pollingApiResponse != null) {
                        println("Polling Success: Data received for requestId: $requestId")
                        return@withTimeoutOrNull ResourceState.Success(pollingApiResponse)
                    } else {
                        println("Polling Warning: Received 200 OK but null body for requestId: $requestId")
                        Log.w("ApiRequestExecutor", "Polling Warning: Received 200 OK but null body for requestId: $requestId")
                        return@withTimeoutOrNull ResourceState.Error("Polling Error: Received 200 OK but null body")
                    }

                } catch (e: IOException) {
                    println("Polling Network Error: ${e.message} for requestId: $requestId")
                    Log.e("ApiRequestExecutor", "Polling Network Error for requestId: $requestId", e)
                    return@withTimeoutOrNull ResourceState.Error("Polling Network Error: ${e.message ?: "Unknown I/O error"}")
                } catch (e: Exception) { // Ловим ошибки парсинга JSON от pollingCall
                    println("Polling Execution or Parsing Error: ${e.message} for requestId: $requestId")
                    Log.e("ApiRequestExecutor", "Polling Execution or Parsing Error for requestId: $requestId", e)
                    return@withTimeoutOrNull ResourceState.Error("Polling Execution Error: ${e.message ?: "An unexpected error occurred"}")
                }
            }
            @Suppress("UNREACHABLE_CODE")
            ResourceState.Error("Polling logic error")
        }

        return result ?: run {
            Log.e("ApiRequestExecutor", "Polling timed out after ${POLLING_TIMEOUT_MS / 1000} seconds for requestId: $requestId")
            ResourceState.Error("Polling timed out after ${POLLING_TIMEOUT_MS / 1000} seconds for requestId: $requestId")
        }
    }

    /**
     * Выполняет обычный API-вызов.
     *
     * @param T Тип ожидаемых данных.
     * @param call Лямбда-функция для выполнения запроса к API (ожидает Response<T>).
     * @return ResourceState<T> Состояние результата: Success, Error или Loading.
     */
    suspend fun <T : Any> executeRequest(
        call: suspend () -> Response<T>
    ): ResourceState<T> = withContext(ioDispatcher) {
        try {
            val response = call()

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return@withContext ResourceState.Success(body)
                } else {
                    return@withContext ResourceState.Error("Empty response body")
                }
            } else {
                return@withContext ResourceState.Error("API Error: ${response.code()} ${response.message()}")
            }
        } catch (e: IOException) {
            return@withContext ResourceState.Error("Network Error: ${e.message ?: "Unknown I/O error"}")
        } catch (e: Exception) {
            Log.e("ApiRequestExecutor", "Execution Error", e)
            return@withContext ResourceState.Error("Execution Error: ${e.message ?: "An unexpected error occurred"}")
        }
    }
}