/*
 * Copyright (C) 2017 The Dagger Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package im.bnw.android.di.core

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentProvider
import android.content.Context
import androidx.fragment.app.Fragment
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber

const val DOES_NOT_IMPLEMENT = "%s does not implement %s"
const val NO_INJECTOR = "No injector was found for %s"

/**
 * Injects core Android types.
 */
@SuppressWarnings("TooGenericExceptionThrown")
object AndroidXInjection {
    /**
     * Injects `activity` if an associated [AndroidInjector] implementation can be found,
     * otherwise throws an [IllegalArgumentException].
     *
     * @throws RuntimeException if the [Application] doesn't implement [HasAndroidInjector].
     */
    fun inject(activity: Activity) {
        val application = activity.application
        if (application !is HasAndroidInjector) {
            throw RuntimeException(
                String.format(
                    DOES_NOT_IMPLEMENT,
                    application.javaClass.canonicalName,
                    HasAndroidInjector::class.java.canonicalName
                )
            )
        }
        inject(activity, application as HasAndroidInjector)
    }

    /**
     * Injects `fragment` if an associated [AndroidInjector] implementation can be found,
     * otherwise throws an [IllegalArgumentException].
     *
     * Uses the following algorithm to find the appropriate `AndroidInjector<Fragment>` to
     * use to inject `fragment`:
     *
     *  1. Walks the parent-fragment hierarchy to find the a fragment
     *  that implements [HasAndroidInjector], and if none do
     *  2. Uses the `fragment`'s [activity][Fragment.getActivity] if it implements
     * [HasAndroidInjector], and if not
     *  3. Uses the [Application] if it implements [HasAndroidInjector].
     *
     * If none of them implement [HasAndroidInjector], a [IllegalArgumentException] is
     * thrown.
     *
     * @throws IllegalArgumentException if no parent fragment, activity, or application implements
     * [HasAndroidInjector].
     */
    fun inject(fragment: Fragment) {
        val hasAndroidInjector =
            findHasAndroidInjectorForFragment(fragment)
        Timber.d(
            "An injector for %s was found in %s",
            fragment.javaClass.canonicalName,
            hasAndroidInjector.javaClass.canonicalName
        )
        inject(fragment, hasAndroidInjector)
    }

    @SuppressWarnings("ReturnCount")
    private fun findHasAndroidInjectorForFragment(fragment: Fragment): HasAndroidInjector {
        var tempFragment: Fragment? = fragment.parentFragment
        while (tempFragment != null) {
            if (tempFragment is HasAndroidInjector) {
                return tempFragment
            }
            tempFragment = tempFragment.parentFragment
        }
        val activity: Activity? = fragment.activity
        if (activity is HasAndroidInjector) {
            return activity
        }
        if (activity!!.application is HasAndroidInjector) {
            return activity.application as HasAndroidInjector
        }
        throw IllegalArgumentException(
            String.format(
                NO_INJECTOR,
                fragment.javaClass.canonicalName
            )
        )
    }

    /**
     * Injects `service` if an associated [AndroidInjector] implementation can be found,
     * otherwise throws an [IllegalArgumentException].
     *
     * @throws RuntimeException if the [Application] doesn't implement [HasAndroidInjector].
     */
    fun inject(service: Service) {
        val application = service.application
        if (application !is HasAndroidInjector) {
            throw RuntimeException(
                String.format(
                    DOES_NOT_IMPLEMENT,
                    application.javaClass.canonicalName,
                    HasAndroidInjector::class.java.canonicalName
                )
            )
        }
        inject(service, application as HasAndroidInjector)
    }

    /**
     * Injects `broadcastReceiver` if an associated [AndroidInjector] implementation can
     * be found, otherwise throws an [IllegalArgumentException].
     *
     * @throws RuntimeException if the [Application] from [Context.getApplicationContext]
     * doesn't implement [HasAndroidInjector].
     */
    fun inject(broadcastReceiver: BroadcastReceiver, context: Context) {
        val application = context.applicationContext as Application
        if (application !is HasAndroidInjector) {
            throw RuntimeException(
                String.format(
                    DOES_NOT_IMPLEMENT,
                    application.javaClass.canonicalName,
                    HasAndroidInjector::class.java.canonicalName
                )
            )
        }
        inject(broadcastReceiver, application as HasAndroidInjector)
    }

    /**
     * Injects `contentProvider` if an associated [AndroidInjector] implementation can be
     * found, otherwise throws an [IllegalArgumentException].
     *
     * @throws RuntimeException if the [Application] doesn't implement [HasAndroidInjector].
     */
    fun inject(contentProvider: ContentProvider) {
        val application = contentProvider.context!!.applicationContext as Application
        if (application !is HasAndroidInjector) {
            throw RuntimeException(
                String.format(
                    DOES_NOT_IMPLEMENT,
                    application.javaClass.canonicalName,
                    HasAndroidInjector::class.java.canonicalName
                )
            )
        }
        inject(contentProvider, application as HasAndroidInjector)
    }

    private fun inject(target: Any, hasAndroidInjector: HasAndroidInjector) {
        val androidInjector = hasAndroidInjector.androidInjector()
        androidInjector.inject(target)
    }
}
