package org.yarokovisty.service

class FakeDownloadRepository {
    fun downloadFakeFile(callback: (DownloadState) -> Unit) {

        callback(DownloadState.Started)

        try {
            Thread.sleep(1500)

            for (i in 1..100) {
                Thread.sleep(200)
                callback(DownloadState.Progress(i))
            }

            callback(DownloadState.Completed)

        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }
}

sealed class DownloadState {
    object Started : DownloadState()
    class Progress(val percent: Int) : DownloadState()
    object Completed : DownloadState()
}
