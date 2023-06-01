package com.example.bluromatic

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.bluromatic.workers.blurBitmap
import com.example.bluromatic.workers.makeStatusNotification
import com.example.bluromatic.workers.writeBitmapToFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val TAG = "BlurWorker"

class BlurWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(
    appContext,
    params
) {
    override suspend fun doWork(): Result {

        makeStatusNotification(
            applicationContext.resources.getString(R.string.blurring_image),
            applicationContext
        )
        return withContext(Dispatchers.IO) {
            try {
                //1. sleep a bit
                delay(DELAY_TIME_MILLIS)

                //2. take picture from drawable
                val picture = BitmapFactory.decodeResource(
                    applicationContext.resources,
                    R.drawable.android_cupcake
                )
                //3. do some work with it (in out case blur the image)
                val output = blurBitmap(picture, 1)

                //4. Write bitmap to a temp file
                val outputUri = writeBitmapToFile(applicationContext, output)

                //5. display a notification message to the user
                makeStatusNotification(
                    "Output is $outputUri",
                    applicationContext
                )

                Result.success()

            } catch (throwable: Throwable) {
                Log.e(
                    TAG,
                    applicationContext.resources.getString(R.string.error_applying_blur),
                    throwable
                )

                Result.failure()
            }
        }
    }
}