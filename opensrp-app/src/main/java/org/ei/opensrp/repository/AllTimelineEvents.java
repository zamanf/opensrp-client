package org.ei.opensrp.repository;

import org.ei.opensrp.domain.TimelineEvent;
import org.ei.opensrp.repository.cloudant.TimelineEventsModel;

import java.util.List;

public class AllTimelineEvents {
    private TimelineEventRepository repository;

    TimelineEventsModel mTimelineEventsModel = org.ei.opensrp.Context.getInstance().timelineEventsModel();

    public AllTimelineEvents(TimelineEventRepository repository) {
        this.repository = repository;
    }

    public List<TimelineEvent> forCase(String caseId) {
        return mTimelineEventsModel.allFor(caseId);
    }

    public void add(TimelineEvent timelineEvent) {
        mTimelineEventsModel.add(timelineEvent);
    }
}
