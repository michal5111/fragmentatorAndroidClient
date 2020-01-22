package com.example.springfragmenterclient.activities

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.model.FragmentRequest
import com.example.springfragmenterclient.model.Movie
import com.example.springfragmenterclient.repositories.FragmentRequestRepository
import com.example.springfragmenterclient.rest.responses.ConversionStatus
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class FragmentRequestViewModel
@Inject constructor(private val fragmentRequestRepository: FragmentRequestRepository) :
    ViewModel() {

    lateinit var movie: Movie
    var message: String = ""
    var percent: Double = 0.0
    var to: Double = 0.0
    var lastDownload: Long = -1L
    var lastShare: Long = -1L
    lateinit var fileName: String
    lateinit var fragmentRequest: FragmentRequest
    val compositeDisposable = CompositeDisposable()
    lateinit var downloadManager: DownloadManager

    fun saveFragmentRequest(fragmentRequest: FragmentRequest): Single<FragmentRequest> =
        fragmentRequestRepository.save(fragmentRequest)
            .map {
                this.fragmentRequest = it
                return@map it
            }

    fun requestFragment(id: Long): Observable<ConversionStatus> =
        fragmentRequestRepository.fragmentRequest(id)
            .doOnNext {
                message = message.plus(it.logLine).plus("\n")
                if (it.eventType == "to") {
                    to = it.timeLength!!
                } else if (it.eventType == "log") {
                    if (it.logLine!!.contains("frame=")) {
                        val offset = it.logLine.lastIndexOf("time=")
                        val time = it.logLine.substring(offset + 5, offset + 16)
                        percent = Fragmentator4000.timeToSeconds(time) * 100.0 / to
                    }
                } else if (it.eventType == "complete") {
                    fileName = it.logLine!!
                }
            }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun downloadManagerEnqueueForSharing(fileName: String, context: Context) =
        downloadManager.enqueue(
            DownloadManager.Request(("${Fragmentator4000.fragmentsUrl}/$fileName").toUri())
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("Fragment: " + movie.fileName + fragmentRequest.startLineId)
                .setDescription(movie.fileName)
                .setDestinationInExternalFilesDir(
                    context,
                    "cache",
                    fileName
                )
        )

    fun downloadManagerEnqueueForDownload(fileName: String, context: Context) =
        downloadManager.enqueue(
            DownloadManager.Request(("${Fragmentator4000.fragmentsUrl}/$fileName").toUri())
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("Fragment: " + movie.fileName + fragmentRequest.startLineId)
                .setDescription(movie.fileName)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    fileName
                )
        )
}