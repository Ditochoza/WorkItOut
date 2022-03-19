package es.ucm.fdi.workitout.utils

sealed class DatabaseResult<T> {
    data class Success<T>(val data: T) : DatabaseResult<T>()
    data class Failed<T>(val resMessage: Int) : DatabaseResult<T>()

    companion object {
        fun <T> success(data: T) = Success(data)
        fun <T> failed(resMessage: Int) = Failed<T>(resMessage)
    }
}