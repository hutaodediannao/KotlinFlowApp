package com.example.kotlinflowapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var btn: Button
    private lateinit var tv: TextView
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv = findViewById(R.id.textView)
        btn = findViewById(R.id.button)
        btn.setOnClickListener {
//            test()
//            test2()
            test3()
        }
    }

    private suspend fun showText(text: String) {
        Thread.sleep(1000)
        Log.i(TAG, "showText: $text")
    }

    private fun test3() {
//        runBlocking {
        val singThread = newSingleThreadContext("thread-hutao")
        GlobalScope.launch {
            launch(singThread) {
                repeat(3) {
                    val str = "hello-$it, ${Thread.currentThread().name}"
                    showText(str)
//                        yield()
                }
            }
            launch(singThread) {
                repeat(2) {
                    val str = "ok-$it, ${Thread.currentThread().name}"
                    showText(str)
//                        yield()
                }
            }
        }
//        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun test2() {
        CoroutineScope(newSingleThreadContext("hello-Thread")).launch {
            Log.i(TAG, "000: threadName ======> ${Thread.currentThread().name}")
            launch(Dispatchers.Default) {
                Log.i(TAG, "001: default ThreadName:${Thread.currentThread().name}")
            }
            launch(Dispatchers.IO) {
                Log.i(TAG, "002: default ThreadName:${Thread.currentThread().name}")
            }
            launch {
                Log.i(TAG, "003: default ThreadName:${Thread.currentThread().name}")
            }
            launch(Dispatchers.Main) {
                Log.i(TAG, "004: default ThreadName:${Thread.currentThread().name}")
            }
            repeat(5) {
                Log.i(TAG, "test2: it = $it")
            }
        }


    }

    private fun test() {
        job = CoroutineScope(Dispatchers.Main).launch {
            val list = mutableListOf("oppo", "xiaomi", "nokia", "huawei", "vivo")
            list.asFlow()
                .filter {
                    Log.i(TAG, ": it = $it, Thread:${Thread.currentThread().name}")
                    !it.endsWith("0")
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    Log.i(TAG, "collect: $it, Thread:${Thread.currentThread().name}")
                    tv.text = it
                }
                .launchIn(CoroutineScope(Dispatchers.Main))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!job.isCancelled) {
            job.cancel()
        }
    }
}