package es.ucm.fdi.workitout.model

sealed class DatabaseResult<T> {
    data class SuccessData<T>(val data: T) : DatabaseResult<T>()
    data class SuccessMessage<T>(val resMessage: Int) : DatabaseResult<T>()
    data class Failed<T>(val resMessage: Int) : DatabaseResult<T>()

    companion object {
        fun <T> successData(data: T) = SuccessData(data)
        fun <T> successMessage(resMessage: Int) = SuccessMessage<T>(resMessage)
        fun <T> failed(resMessage: Int) = Failed<T>(resMessage)
    }
}