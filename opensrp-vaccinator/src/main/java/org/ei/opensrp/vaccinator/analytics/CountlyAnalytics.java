package org.ei.opensrp.vaccinator.analytics;

import android.app.Activity;

import org.ei.opensrp.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import ly.count.android.sdk.Countly;

import util.Utils;

/**
 * Created by Safwan on 5/3/2016.
 */
public class CountlyAnalytics {

    // private static int count = 1;
   /* private Context context;
    Resources res = Context.getInstance().applicationContext().getResources();*/

    public static void startAnalytics(Activity activity, Events event, HashMap<String, String> segmentation) {
        Countly.sharedInstance().onStart(activity);
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss a zzz");
        segmentation.put("user", Context.getInstance().anmService().fetchDetails().name());
        segmentation.put("Start Time", ft.format(date));
        HashMap<String, String> providerDetails = new HashMap<String, String>();
        providerDetails = Utils.providerDetails();
        providerDetails.keySet().removeAll(segmentation.keySet());
        segmentation.putAll(providerDetails);

        if(event.equals(Events.LOGIN)) {
            segmentation.put("event", Events.LOGIN.toString());
        }

        else if(event.equals(Events.CHILD_ENROLLMENT)) {
            segmentation.put("event", Events.CHILD_ENROLLMENT.toString());
        }

        else if(event.equals(Events.CHILD_FOLLOWUP)) {
            segmentation.put("event", Events.CHILD_FOLLOWUP.toString());
        }

        else if(event.equals(Events.WOMAN_ENROLLMENT)) {
            segmentation.put("event", Events.WOMAN_ENROLLMENT.toString());
        }

        else if(event.equals(Events.WOMAN_FOLLOWUP)) {
            segmentation.put("event", Events.WOMAN_FOLLOWUP.toString());
        }

        Countly.sharedInstance().recordEvent(event.toString(), segmentation, 1);
    }


    public static void stopAnalytics(){
        Countly.sharedInstance().onStop();
    }
}
