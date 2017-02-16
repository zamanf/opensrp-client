package org.ei.opensrp.core.db.domain;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.view.contract.SmartRegisterClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maimoona on 1/26/2017.
 */

public class ClientEvent {

    private Client client;
    private List<Event>  events;

    public ClientEvent(Client client, List<Event>  events){
        this.client = client;
        this.events = events;
    }

    public List<Event> getEvents() {
        if (events == null){
            events = new ArrayList<>();
        }
        return events;
    }

    public Event getLatestEvent(String eventTypeRegex){
        for (Event e: getEvents()) {
            if (e.getEventType().matches(eventTypeRegex)){
                return e;
            }
        }
        return null;
    }

    public String findObsValue(String parentId, boolean nonEmpty, String... fieldIds){
        if (events != null)
        for (Event e: events){
            Obs o = null;
            if ((o = e.findObs(parentId, nonEmpty, fieldIds)) != null){
                if (StringUtils.isNotBlank(o.getValue(true))){
                    return o.getValue(true);
                }
            }
        }
        return "";
    }

    public void addEvent(Event e){
        if (events == null){
            events = new ArrayList<>();
        }
        events.add(e);
    }

    public Client getClient() {
        return client;
    }
}
