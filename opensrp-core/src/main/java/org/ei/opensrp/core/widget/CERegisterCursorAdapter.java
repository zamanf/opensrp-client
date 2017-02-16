package org.ei.opensrp.core.widget;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.core.db.domain.ClientEvent;
import org.ei.opensrp.core.db.repository.RegisterRepository;
import org.ei.opensrp.core.template.RegisterClientsProvider;

import java.util.ArrayList;
import java.util.List;

public class CERegisterCursorAdapter <T> extends RegisterCursorAdapter {
    public CERegisterCursorAdapter(Context context, RegisterClientsProvider listItemProvider) {
        super(context, listItemProvider);
    }

    private T getCurrentPositionItem(Cursor cursor){
        int pos = cursor.getPosition();
        // Log.v(getClass().getName(), "CURSOR ON "+pos+" AND CURR PAGE = "+currentPage);

        ClientEvent data = RegisterRepository.convertToClientEvent(cursor);
        currentPage.add(data);

        return (T) currentPage.get(pos);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.i(getClass().getName(), "Creating view from cursor");

        listItemProvider.getView(getCurrentPositionItem(cursor), view, null/*todo*/);
    }
}
