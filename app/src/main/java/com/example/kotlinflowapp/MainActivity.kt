package com.example.kotlinflowapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.math.log

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
//            test3()
//            test4()
            test5()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun test5() {
        val th = newSingleThreadContext("myThread")
        job = CoroutineScope(Dispatchers.Main).launch {
            launch(th) {
                repeat(10) {
                    yield()
                    Log.i(TAG, "$it")
                    delay(1000)
                }
            }
            launch(th) {
                repeat(10) {
                    Log.i(TAG, "test5: 休息一秒钟...")
                    delay(1000)
                    yield()
                }
            }
        }
    }

    private fun test4() {
        val th = newSingleThreadContext("hello-thread-01")
        job = CoroutineScope(th).launch {
            try {
                launch(th) {
                    repeat(5) {
                        Log.i(TAG, "001: $it, ${Thread.currentThread().name}")
                        delay(1000)
                        yield()
                    }
                }
                launch(th) {
                    repeat(2) {
                        Log.i(TAG, "002: $it, ${Thread.currentThread().name}")
                        delay(3000)
                        yield()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i(TAG, "test4: 异常:${e.message}")
            }
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
            Log.i(TAG, "onDestroy: 开始取消job")
            job.cancel()
            Log.i(TAG, "onDestroy: 已经执行cancel job")
        }
    }
}