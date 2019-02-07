/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.example.muzpal.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.RetryStrategy;

public class ProductFirebaseJobService extends JobService {

    private AsyncTask<Void, Void, Void> mFetchProductTask;

    // The entry point to our Job.
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        mFetchProductTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                ProductSyncTask.syncProduct(context);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                // Once the product data is sync'd, call jobFinished with the appropriate arguments
                jobFinished(jobParameters, false);
            }
        };
        mFetchProductTask.execute();
        return true;
    }

    // Called when the scheduling engine has decided to interrupt the execution of a running job
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mFetchProductTask != null) {
            mFetchProductTask.cancel(true);
        }
        return true;
    }
}