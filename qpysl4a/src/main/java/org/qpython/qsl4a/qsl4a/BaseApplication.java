/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.qpython.qsl4a.qsl4a;

import android.app.Application;

import org.qpython.qsl4a.qsl4a.interpreter.InterpreterConfiguration;
import org.qpython.qsl4a.qsl4a.trigger.TriggerRepository;

public class BaseApplication extends Application {

    private final FutureActivityTaskExecutor mTaskExecutor = new FutureActivityTaskExecutor(this);
    private TriggerRepository mTriggerRepository;

    protected InterpreterConfiguration mConfiguration;

    public FutureActivityTaskExecutor getTaskExecutor() {
        return mTaskExecutor;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mConfiguration = new InterpreterConfiguration(this);
        mConfiguration.startDiscovering();
        mTriggerRepository = new TriggerRepository(this);
    }

    public InterpreterConfiguration getInterpreterConfiguration() {
        return mConfiguration;
    }

    public TriggerRepository getTriggerRepository() {
        return mTriggerRepository;
    }
}
