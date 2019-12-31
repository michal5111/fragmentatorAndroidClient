package com.example.springfragmenterclient.activities

import androidx.lifecycle.ViewModel
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.entities.FragmentRequest
import com.example.springfragmenterclient.repositories.FragmentRequestRepository
import com.example.springfragmenterclient.rest.responses.ConversionStatus
import io.reactivex.Observable
import io.reactivex.Single

class FragmentRequestViewModel: ViewModel() {
    private val fragmentRequestRepository = FragmentRequestRepository()

    var message: String = ""
    var percent: Double = 0.0
    var to: Double = 0.0
    var lastDownload: Long = -1L
    var lastShare: Long = -1L
    lateinit var fileName: String
    lateinit var fragmentRequest: FragmentRequest

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
}