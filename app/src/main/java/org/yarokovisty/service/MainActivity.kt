package org.yarokovisty.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Context.JOB_SCHEDULER_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import org.yarokovisty.service.ui.theme.ServiceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ServiceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        StartSampleServiceButton()

                        StartForegroundServiceButton()

                        StartIntentServiceButton()

                        StartJobServiceButton()

                        StartAlarmManagerButton()

                        StartWorkManager()
                    }
                }
            }
        }
    }
}

@Composable
fun StartSampleServiceButton() {
    val context = LocalContext.current

    Button(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        onClick = { context.startService(SampleService.newIntent(context)) }
    ) {
        Text("Service")
    }
}

@Composable
fun StartForegroundServiceButton() {
    val context = LocalContext.current

    Button(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(
                    context,
                    SampleForegroundService.newIntent(context)
                )
            } else {
                context.startService(SampleForegroundService.newIntent(context))
            }

        }
    ) {
        Text("ForegroundService")
    }
}

@Composable
fun StartIntentServiceButton() {
    val context = LocalContext.current

    Button(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(
                    context,
                    SampleIntentService.newIntent(context)
                )
            } else {
                context.startService(SampleIntentService.newIntent(context))
            }
        }
    ) {
        Text("IntentService")
    }
}

@Composable
fun StartJobServiceButton() {
    val context = LocalContext.current

    Button(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        onClick = {
            val componentName = ComponentName(context, SampleJobService::class.java)

            val jobInfo = JobInfo.Builder(SampleJobService.JOB_ID, componentName)
                .setRequiresCharging(true)
                .setPersisted(true)
                .build()

            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.schedule(jobInfo)
        }
    ) {
        Text("JobService")
    }
}

@Composable
fun StartAlarmManagerButton() {
    val context = LocalContext.current

    Button(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        onClick = {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, SampleAlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerTime = System.currentTimeMillis() + 15_000

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    ) {
        Text("AlarmManager")
    }
}

@Composable
fun StartWorkManager() {
    val context = LocalContext.current

    Button(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        onClick = {
            val workManager = WorkManager.getInstance(context)
            workManager.enqueueUniqueWork(
                SampleWorker.WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                SampleWorker.makeRequest(30)
            )
        }
    ) {
        Text("WorkManager")
    }
}
