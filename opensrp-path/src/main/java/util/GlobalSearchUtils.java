package util;

import android.view.View;
import android.widget.ProgressBar;

import org.ei.opensrp.Context;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.view.BackgroundAction;
import org.ei.opensrp.view.LockingBackgroundTask;
import org.ei.opensrp.view.ProgressIndicator;

import static android.view.View.VISIBLE;
import static org.ei.opensrp.AllConstants.OPENSRP_LOCATION_URL_PATH;

/**
 * Created by keyman on 26/01/2017.
 */
public class GlobalSearchUtils {

    public static void backgroundSearch(String searchText, final ProgressBar progressBar) {
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

        task.doActionInBackground(new BackgroundAction<String>() {
            public String actionToDoInBackgroundThread() {
                Response<String> response = globalSearch();
                if(response.isFailure()){
                    return null;
                }else {
                    return response.payload();
                }
            }

            public void postExecuteInUIThread(String result) {
                //afterLoginCheck.onEvent(result);
            }
        });
    }

    public static Response<String> globalSearch() {
        String url = ""; //Get url
        return Context.getInstance().getHttpAgent().fetch(url);
    }
}
