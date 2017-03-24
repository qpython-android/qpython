package org.qpython.qsl4a.qsl4a.trigger;

import android.content.Context;
import android.content.Intent;

import org.qpython.qsl4a.qsl4a.IntentBuilders;
import org.qpython.qsl4a.qsl4a.event.Event;

import java.io.File;

/**
 * A trigger implementation that launches a given script when the event occurs.
 * 
 * @author Felix Arends (felix.arends@gmail.com)
 */
public class ScriptTrigger implements Trigger {
  private static final long serialVersionUID = 1804599219214041409L;
  private final File mScript;
  private final String mEventName;

  public ScriptTrigger(String eventName, File script) {
    mEventName = eventName;
    mScript = script;
  }

  @Override
  public void handleEvent(Event event, Context context) {
    Intent intent = IntentBuilders.buildStartInBackgroundIntent(mScript);
    // This is required since the script is being started from the TriggerService.
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }

  @Override
  public String getEventName() {
    return mEventName;
  }

  public File getScript() {
    return mScript;
  }
}
