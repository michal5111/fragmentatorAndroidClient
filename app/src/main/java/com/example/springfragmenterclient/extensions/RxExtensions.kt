package com.example.springfragmenterclient.extensions

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class RxExtensions {
    operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
        add(disposable)
    }
}