/*
 * Copyright (C) 2018 The Android Open Source Project
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

package android.device.collectors;

import static android.device.collectors.RssSnapshotListener.DROP_CACHE_KEY;
import static android.device.collectors.RssSnapshotListener.OUTPUT_DIR_KEY;
import static android.device.collectors.RssSnapshotListener.PROCESS_NAMES_KEY;
import static android.device.collectors.RssSnapshotListener.PROCESS_SEPARATOR;
import static org.mockito.Mockito.verify;

import android.app.Instrumentation;
import android.os.Bundle;
import androidx.test.runner.AndroidJUnit4;
import com.android.helpers.RssSnapshotHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Android Unit tests for {@link RssSnapshotListener}.
 *
 * To run:
 * atest CollectorDeviceLibTest:android.device.collectors.RssSnapshotListenerTest
 */
@RunWith(AndroidJUnit4.class)
public class RssSnapshotListenerTest {
  @Mock private Instrumentation mInstrumentation;
  @Mock private RssSnapshotHelper mRssSnapshotHelper;

  private RssSnapshotListener mListener;
  private Description mRunDesc;

  private static final String VALID_OUTPUT_DIR = "/data/local/tmp";

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    mRunDesc = Description.createSuiteDescription("run");
  }

  private RssSnapshotListener initListener(Bundle b) {
    RssSnapshotListener listener = new RssSnapshotListener(b, mRssSnapshotHelper);
    listener.setInstrumentation(mInstrumentation);
    return listener;
  }

  @Test
  public void testHelperReceivesProcessNames() throws Exception {
    Bundle b = new Bundle();
    b.putString(PROCESS_NAMES_KEY, "process1" + PROCESS_SEPARATOR + "process2");
    b.putString(OUTPUT_DIR_KEY, VALID_OUTPUT_DIR);

    mListener = initListener(b);

    mListener.testRunStarted(mRunDesc);

    verify(mRssSnapshotHelper).setUp(VALID_OUTPUT_DIR, "process1", "process2");
  }

  @Test
  public void testAdditionalOptions() throws Exception {
    Bundle b = new Bundle();
    b.putString(PROCESS_NAMES_KEY, "process1");
    b.putString(OUTPUT_DIR_KEY, VALID_OUTPUT_DIR);
    b.putString(DROP_CACHE_KEY, "all");
    mListener = initListener(b);

    mListener.testRunStarted(mRunDesc);

    verify(mRssSnapshotHelper).setUp(VALID_OUTPUT_DIR, "process1");
    // DROP_CACHE_KEY values: "pagecache" = 1, "slab" = 2, "all" = 3
    verify(mRssSnapshotHelper).setDropCacheOption(3);
  }
}
