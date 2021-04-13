package com.example.springfragmenterclient.activities

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    private val _messageLiveData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    private var to: Double = 0.0
    private val _percentLiveData: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val messageLiveData: LiveData<String>
        get() = _messageLiveData

    val percentLiveData: LiveData<Int>
        get() = _percentLiveData

    var lastDownload: Long = -1L
    var lastShare: Long = -1L
    val compositeDisposable = CompositeDisposable()
    lateinit var movie: Movie
    lateinit var fileName: String
    lateinit var fragmentRequest: FragmentRequest
    lateinit var downloadManager: DownloadManager

    fun saveFragmentRequest(fragmentRequest: FragmentRequest): Single<FragmentRequest> =
        fragmentRequestRepository.save(fragmentRequest)
            .doOnSuccess {
                this.fragmentRequest = it
            }

    fun requestFragment(id: Long): Observable<ConversionStatus> =
        fragmentRequestRepository.fragmentRequest(id)
            .doOnNext {
                when {
                    it.eventType == "to" && it.timeLength != null -> {
                        to = it.timeLength
                    }
                    it.eventType == "log" && it.logLine != null -> {
                        _messageLiveData.value = _messageLiveData.value.plus(it.logLine).plus("\n")
                        if (it.logLine.contains("frame=")) {
                            val offset = it.logLine.lastIndexOf("time=")
                            val time = it.logLine.substring(offset + 5, offset + 16)
                            _percentLiveData.value =
                                (Fragmentator4000.timeToSeconds(time) * 100.0 / to).toInt()
                        }
                    }
                    it.eventType == "complete" && it.logLine != null -> {
                        fileName = it.logLine
                    }
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

    fun downloadManagerEnqueueForDownload(fileName: String) =
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