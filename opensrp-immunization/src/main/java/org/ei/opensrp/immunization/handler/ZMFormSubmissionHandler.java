package org.ei.opensrp.immunization.handler;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.immunization.child.ChildSmartRegisterActivity;
import org.ei.opensrp.immunization.woman.WomanSmartRegisterActivity;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

/**
 * Created by Maimoona on 2/3/2017.
 */

public class ZMFormSubmissionHandler implements FormSubmissionHandler
{
    private Activity activity;

    public ZMFormSubmissionHandler(Activity activity){
        this.activity = activity;
    }

    @Override
    public void handle(FormSubmission submission) {
        try {
            Log.v(getClass().getName(), "Handing routed form : "+submission.toString());
            String pid = submission.getFieldValue("program_client_id");
            if(StringUtils.isBlank(pid)){
                pid = submission.getFieldValue("existing_program_client_id");
            }
            Intent intent = null;
            if(submission.formName().toLowerCase().contains("child")){
                intent = new Intent(activity, ChildSmartRegisterActivity.class);
            }
            else if(submission.formName().toLowerCase().contains("woman")){
                intent = new Intent(activity, WomanSmartRegisterActivity.class);
            }
            intent.putExtra("program_client_id", pid);
            activity.startActivity(intent);
            activity.finish();
        }
        catch (Exception e){e.printStackTrace();}//todo what to do with it
    }
}
