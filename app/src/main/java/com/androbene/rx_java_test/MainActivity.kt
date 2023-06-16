package com.androbene.rx_java_test

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    var logTv: TextView? = null
    var logString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logTv = findViewById(R.id.tvLog)

//        Observable<Long> observable = Observable.range(50, 10);
//        Observable<Long> observable = Observable.just(50, 60, 70);
        val observable = Observable
            .intervalRange(0, 10, 1000, 500, TimeUnit.MILLISECONDS)
            .mergeWith(Observable.intervalRange(50, 20, 900, 600, TimeUnit.MILLISECONDS))
            .filter { l: Long -> l % 2 == 0L }
            .take(10)
            .zipWith(
                Observable.range(1000, 1000)
            ) { l: Long, i: Int -> l + i }
            .takeUntil { l: Long -> l > 1060 }
        val observer: Observer<Long> = object : Observer<Long> {
            override fun onSubscribe(d: Disposable) {
                addLog("onSubscribe")
            }

            override fun onNext(s: Long) {
                addLog("onNext: $s")
            }

            override fun onError(e: Throwable) {
                addLog("onError: $e")
            }

            override fun onComplete() {
                addLog("onComplete")
            }
        }
        observable.subscribe(observer)
    }

    fun addLog(log: String) {
        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.Main) {
                logString += log + "\n"
                logTv!!.text = logString
            }
        }
    }
}