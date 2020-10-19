package com.example.android.unewsapp.remote

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher,
                            apiCall: suspend () -> Response<T>,
): Resource<T> {
    return withContext(dispatcher) {
        var resource: Resource<T>
        try {
            val response = apiCall.invoke()
            if (response.isSuccessful) {
                resource = successData(response)
            } else {
                resource = errorDataAsync(response)
            }
        } catch (t: Throwable) {
            resource = exceptionDataAsync(t)

        }
        resource
    }
}

fun <T> exceptionDataAsync(t: Throwable): Resource<T> {
    return Resource(Resource.ERROR, null, exceptionData(t))
}

fun exceptionData(error: Throwable): ErrorEntity = when {


    error is SocketTimeoutException ->
        ErrorEntity(ErrorEntity.ERROR_CODE_TIMEOUT)

    error is UnsupportedOperationException && error.message == "JsonNull" ->
        ErrorEntity(ErrorEntity.ERROR_CODE_NO_CONTENT)

    error is UnknownHostException ->
        ErrorEntity(ErrorEntity.ERROR_NO_INTERNET)

    else -> ErrorEntity(ErrorEntity.ERROR_UNKNOWN_EXCEPTION).apply {
        errorDesc = error.message
        error.printStackTrace()
    }
}

fun <T> errorData(response: Response<T>): ErrorEntity {
    val gson = Gson()
    val errorEntity: ErrorEntity
    var errorBodyString = ""
    response.errorBody()?.let {
        errorBodyString = it.string()
    }
    //TODO: Process error codes
    errorEntity = ErrorEntity(ErrorEntity.ERROR_UNKNOWN_EXCEPTION)
    val errorBody = response.errorBody()?.string()
    if (errorBody != null) {
        try {
            errorEntity.errorDesc =
                gson.fromJson(errorBody, NetworkErrorResponse::class.java)
                    .errors._global.first().message
        } catch (ex: Throwable) {
            Log.d("CoroutineHandler", "ErrorBody unknown format")
        }
    }
    return errorEntity
}

fun <T> errorDataAsync(response: Response<T>): Resource<T> {
    return Resource(Resource.ERROR, null, errorData(response))
}

fun <T> successData(response: Response<T>): Resource<T> {
    return if (response.code() != 204 && response.body() == null) {
        Resource(Resource.ERROR, null, ErrorEntity(ErrorEntity.ERROR_CODE_NO_CONTENT))
    } else {
        Resource(Resource.SUCCESS, response.body(), null)
    }
}