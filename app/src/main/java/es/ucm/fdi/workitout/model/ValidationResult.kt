package es.ucm.fdi.workitout.model

sealed class ValidationResult {
    object Success : ValidationResult()
    object Failed : ValidationResult()
    data class FailedSnackbar(val snackbar: Pair<Int,Array<String>>) : ValidationResult()
    data class FailedToast(val resMessage: Int) : ValidationResult()

    companion object {
        fun success() = Success
        fun failed() = Failed
        fun failedSnackbar(snackbar: Pair<Int,Array<String>>) = FailedSnackbar(snackbar)
        fun failedToast(resMessage: Int) = FailedToast(resMessage)
    }
}