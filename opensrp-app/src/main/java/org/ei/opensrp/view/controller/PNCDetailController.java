package org.ei.opensrp.view.controller;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;

import org.ei.opensrp.AllConstants;
import org.ei.opensrp.application.OpenSRPApplication;
import org.ei.opensrp.db.adapters.BeneficiariesAdapter;
import org.ei.opensrp.db.adapters.EligibleCoupleRepository;
import org.ei.opensrp.db.adapters.TimelineEventRepository;
import org.ei.opensrp.domain.EligibleCouple;
import org.ei.opensrp.domain.Mother;
import org.ei.opensrp.util.DateUtil;
import org.ei.opensrp.util.TimelineEventComparator;
import org.ei.opensrp.view.activity.CameraLaunchActivity;
import org.ei.opensrp.view.contract.CoupleDetails;
import org.ei.opensrp.view.contract.LocationDetails;
import org.ei.opensrp.view.contract.PregnancyOutcomeDetails;
import org.ei.opensrp.view.contract.TimelineEvent;
import org.ei.opensrp.view.contract.pnc.PNCDetail;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import static org.ei.opensrp.AllConstants.ENTITY_ID;
import static org.ei.opensrp.AllConstants.WOMAN_TYPE;

public class PNCDetailController {
    private final Context context;
    private final String caseId;

    @Inject
    private EligibleCoupleRepository allEligibleCouples;

    @Inject
    private BeneficiariesAdapter allBeneficiaries;

    @Inject
    private TimelineEventRepository allTimelineEvents;

    public PNCDetailController(Context context, String caseId) {
        OpenSRPApplication.getInstance().inject(this);
        this.context = context;
        this.caseId = caseId;
    }

    @JavascriptInterface
    public String get() {
        Mother mother = allBeneficiaries.findMotherWithOpenStatus(caseId);
        EligibleCouple couple = allEligibleCouples.findByCaseID(mother.ecCaseId());

        LocalDate deliveryDate = LocalDate.parse(mother.referenceDate());
        Days postPartumDuration = Days.daysBetween(deliveryDate, DateUtil.today());

        PNCDetail detail = new PNCDetail(caseId, mother.thayiCardNumber(),
                new CoupleDetails(couple.wifeName(), couple.husbandName(), couple.ecNumber(), couple.isOutOfArea())
                        .withCaste(couple.getDetail("caste"))
                        .withEconomicStatus(couple.getDetail("economicStatus"))
                        .withPhotoPath(couple.photoPath()),
                new LocationDetails(couple.village(), couple.subCenter()),
                new PregnancyOutcomeDetails(deliveryDate.toString(), postPartumDuration.getDays()))
                .addTimelineEvents(getEvents())
                .addExtraDetails(mother.details());

        return new Gson().toJson(detail);
    }

    @JavascriptInterface
    public void takePhoto() {
        Intent intent = new Intent(context, CameraLaunchActivity.class);
        intent.putExtra(AllConstants.TYPE, WOMAN_TYPE);
        Mother mother = allBeneficiaries.findMotherWithOpenStatus(caseId);
        intent.putExtra(ENTITY_ID, mother.ecCaseId());
        context.startActivity(intent);
    }

    @JavascriptInterface
    private List<TimelineEvent> getEvents() {
        List<org.ei.opensrp.domain.TimelineEvent> events = allTimelineEvents.forCase(caseId);
        List<TimelineEvent> timelineEvents = new ArrayList<TimelineEvent>();

        Collections.sort(events, new TimelineEventComparator());

        for (org.ei.opensrp.domain.TimelineEvent event : events) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd-MM-YYYY");
            timelineEvents.add(new TimelineEvent(event.type(), event.title(), new String[]{event.detail1(), event.detail2()}, event.referenceDate().toString(dateTimeFormatter)));
        }

        return timelineEvents;
    }
}
