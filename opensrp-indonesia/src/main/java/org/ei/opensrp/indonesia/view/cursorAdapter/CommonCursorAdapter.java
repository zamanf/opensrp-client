package org.ei.opensrp.indonesia.view.cursorAdapter;


        import android.content.Context;
        import android.database.Cursor;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.CursorAdapter;
        import android.widget.FrameLayout;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.TextView;

        import com.google.gson.Gson;
        import com.google.gson.reflect.TypeToken;

        import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
        import org.ei.opensrp.indonesia.R;

        import java.util.Map;

        import static org.ei.opensrp.util.StringUtil.humanize;

/**
 * Created by raihan on 11/16/15.
 */

public class CommonCursorAdapter extends CursorAdapter {

    public CommonCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.smart_register_ki_client, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

}

