package util;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.ei.opensrp.Context;
import org.ei.opensrp.DristhiConfiguration;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.view.BackgroundAction;
import org.ei.opensrp.view.LockingBackgroundTask;
import org.ei.opensrp.view.ProgressIndicator;
import org.json.JSONArray;
import org.json.JSONException;

import static android.view.View.VISIBLE;

/**
 * Created by keyman on 26/01/2017.
 */
public class GlobalSearchUtils {

    public static void backgroundSearch(final String name, final Listener<JSONArray> listener,  final ProgressBar progressBar, final View searchButton) {
        LockingBackgroundTask task = new LockingBackgroundTask(new ProgressIndicator() {
            @Override
            public void setVisible() {
                progressBar.setVisibility(VISIBLE);
            }

            @Override
            public void setInvisible() {
                progressBar.setVisibility(View.GONE);
            }
        });

        task.doActionInBackground(new BackgroundAction<JSONArray>() {
            public JSONArray actionToDoInBackgroundThread() {
                Response<String> response = globalSearch(name);
                if(response.isFailure()){
                    return null;
                }else {
                    try {
                        JSONArray jsonArray = new JSONArray(response.payload());
                        return jsonArray;
                    }catch (JSONException e){
                        Log.e(getClass().getName(), "", e);
                        return null;
                    }

                }
            }

            public void postExecuteInUIThread(JSONArray result) {
                listener.onEvent(result);
                searchButton.setEnabled(true);
            }
        });
    }

    public static Response<String> globalSearch(String name) {
        Context context = Context.getInstance();
        DristhiConfiguration configuration = context.configuration();
        String baseUrl = configuration.dristhiBaseURL();
        String uri = baseUrl + "/rest/search/search?name="+name;

        Response<String> response = context.getHttpAgent().fetch(uri);
        return response;
    }
}
