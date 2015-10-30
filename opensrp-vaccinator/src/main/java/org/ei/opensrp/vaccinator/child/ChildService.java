package org.ei.opensrp.vaccinator.child;

import android.util.Log;

import org.ei.opensrp.domain.TimelineEvent;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.repository.AllBeneficiaries;
import org.ei.opensrp.repository.AllTimelineEvents;
import org.joda.time.LocalDate;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 29-Oct-15.
 */
public class ChildService {

    private AllBeneficiaries allBeneficiaries;
    private AllTimelineEvents allTimelines;
    public ChildService (AllBeneficiaries allBeneficiaries,
             AllTimelineEvents allTimelines){
this.allBeneficiaries=allBeneficiaries;
        this.allTimelines=allTimelines;

    }

    public void followup(FormSubmission submission){
        //#TODO :add details into string then into form submission.
        HashMap<String , String>  map=new HashMap<String , String >();
        map.put("center_gps",submission.getFieldValue("center_gps")!=null ?submission.getFieldValue("center_gps"):"");
        //retro vaccines
        map.put("retro_vaccines",submission.getFieldValue("vaccines")!=null ?submission.getFieldValue("vaccines"):"");
        map.put("retro_bcg_date",submission.getFieldValue("bcg")!=null ?submission.getFieldValue("bcg"):"");
        map.put("retro_opv_0_date",submission.getFieldValue("opv_0_date")!=null ?submission.getFieldValue("opv_0_date"):"");
        map.put("retro_opv_0_dose",submission.getFieldValue("opv_0_dose")!=null ?submission.getFieldValue("opv_0_dose"):"");
        map.put("retro_pcv_1_date",submission.getFieldValue("pcv_1_date")!=null ?submission.getFieldValue("pcv_1_date"):"");
        map.put("retro_pcv_1_dose",submission.getFieldValue("pcv_1_dose")!=null ?submission.getFieldValue("pcv_1_dose"):"");

        map.put("retro_opv_1_date",submission.getFieldValue("opv_1_date")!=null ?submission.getFieldValue("opv_1_date"):"");
        map.put("retro_opv_1_dose",submission.getFieldValue("opv_1_dose")!=null ?submission.getFieldValue("opv_1_dose"):"");

        map.put("retro_pentavalent_1_date",submission.getFieldValue("pentavalent_1_date")!=null ?submission.getFieldValue("pentavalent_1_date"):"");
        map.put("retro_pentavalent_1_dose",submission.getFieldValue("pentavalent_1_dose")!=null ?submission.getFieldValue("pentavalent_1_dose"):"");

        map.put("retro_pcv_2_date",submission.getFieldValue("pcv_2_date")!=null ?submission.getFieldValue("pcv_2_date"):"");
        map.put("retro_pcv_2_dose",submission.getFieldValue("pcv_2_dose")!=null ?submission.getFieldValue("pcv_2_dose"):"");

        map.put("retro_opv_2_date",submission.getFieldValue("opv_2_date")!=null ?submission.getFieldValue("opv_2_date"):"");
        map.put("retro_opv_2_dose",submission.getFieldValue("opv_2_dose")!=null ?submission.getFieldValue("opv_2_dose"):"");

        map.put("retro_pcv_3_date",submission.getFieldValue("pcv_3_date")!=null ?submission.getFieldValue("pcv_3_date"):"");
        map.put("retro_pcv_3_dose",submission.getFieldValue("pcv_3_dose")!=null ?submission.getFieldValue("pcv_3_dose"):"");

        map.put("retro_opv_3_date",submission.getFieldValue("opv_3_date")!=null ?submission.getFieldValue("opv_3_date"):"");
        map.put("retro_opv_3_dose",submission.getFieldValue("opv_3_dose")!=null ?submission.getFieldValue("opv_3_dose"):"");

        map.put("retro_pentavalent_3_date",submission.getFieldValue("pentavalent_3_date")!=null ?submission.getFieldValue("pentavalent_3_date"):"");
        map.put("retro_pentavalent_3_dose",submission.getFieldValue("pentavalent_3_dose")!=null ?submission.getFieldValue("pentavalent_3_dose"):"");

        map.put("retro_measles_1_date",submission.getFieldValue("measles_1_date")!=null ?submission.getFieldValue("measles_1_date"):"");
        map.put("retro_measles_1_dose",submission.getFieldValue("measles_1_dose")!=null ?submission.getFieldValue("measles_1_dose"):"");

        map.put("retro_measles_2_date",submission.getFieldValue("measles_2_date")!=null ?submission.getFieldValue("measles_2_date"):"");
        map.put("retro_measles_2_dose",submission.getFieldValue("measles_2_dose")!=null ?submission.getFieldValue("measles_2_dose"):"");

        //today's vaccines
        map.put("current_vaccines",submission.getFieldValue("vaccines_2")!=null ?submission.getFieldValue("vaccines_2"):"");
        map.put("current_vaccines",submission.getFieldValue("vaccination_date")!=null ?submission.getFieldValue("vaccination_date"):"");

        //today vaccines today
        map.put("today_vaccines",submission.getFieldValue("vaccines")!=null ?submission.getFieldValue("vaccines"):"");
        map.put("today_bcg_date",submission.getFieldValue("bcg_today")!=null ?submission.getFieldValue("bcg_today"):"");
        map.put("today_opv_0_date",submission.getFieldValue("opv_0_date_today")!=null ?submission.getFieldValue("opv_0_date_today"):"");
        map.put("today_opv_0_dose",submission.getFieldValue("opv_0_dose_today")!=null ?submission.getFieldValue("opv_0_dose_today"):"");
        map.put("today_pcv_1_date",submission.getFieldValue("pcv_1_date_today")!=null ?submission.getFieldValue("pcv_1_date_today"):"");
        map.put("today_pcv_1_dose",submission.getFieldValue("pcv_1_dose_today")!=null ?submission.getFieldValue("pcv_1_dose_today"):"");

        map.put("today_opv_1_date",submission.getFieldValue("opv_1_date_today")!=null ?submission.getFieldValue("opv_1_date_today"):"");
        map.put("today_opv_1_dose",submission.getFieldValue("opv_1_dose_today")!=null ?submission.getFieldValue("opv_1_dose_today"):"");

        map.put("today_pentavalent_1_date",submission.getFieldValue("pentavalent_1_date_today")!=null ?submission.getFieldValue("pentavalent_1_date_today"):"");
        map.put("today_pentavalent_1_dose",submission.getFieldValue("pentavalent_1_dose_today")!=null ?submission.getFieldValue("pentavalent_1_dose_today"):"");

        map.put("today_pcv_2_date",submission.getFieldValue("pcv_2_date_today")!=null ?submission.getFieldValue("pcv_2_date_today"):"");
        map.put("today_pcv_2_dose",submission.getFieldValue("pcv_2_dose_today")!=null ?submission.getFieldValue("pcv_2_dose_today"):"");

        map.put("today_opv_2_date",submission.getFieldValue("opv_2_date_today")!=null ?submission.getFieldValue("opv_2_date_today"):"");
        map.put("today_opv_2_dose",submission.getFieldValue("opv_2_dose_today")!=null ?submission.getFieldValue("opv_2_dose_today"):"");

        map.put("today_pcv_3_date",submission.getFieldValue("pcv_3_date_today")!=null ?submission.getFieldValue("pcv_3_date_today"):"");
        map.put("today_pcv_3_dose",submission.getFieldValue("pcv_3_dose_today")!=null ?submission.getFieldValue("pcv_3_dose_today"):"");

        map.put("today_opv_3_date",submission.getFieldValue("opv_3_date_today")!=null ?submission.getFieldValue("opv_3_date_today"):"");
        map.put("today_opv_3_dose",submission.getFieldValue("opv_3_dose_today")!=null ?submission.getFieldValue("opv_3_dose_today"):"");

        map.put("today_pentavalent_3_date",submission.getFieldValue("pentavalent_3_date_today")!=null ?submission.getFieldValue("pentavalent_3_date_today"):"");
        map.put("today_pentavalent_3_dose",submission.getFieldValue("pentavalent_3_dose_today")!=null ?submission.getFieldValue("pentavalent_3_dose_today"):"");

        map.put("today_measles_1_date",submission.getFieldValue("measles_1_date_today")!=null ?submission.getFieldValue("measles_1_date_today"):"");
        map.put("today_measles_1_dose",submission.getFieldValue("measles_1_dose_today")!=null ?submission.getFieldValue("measles_1_dose_today"):"");

        map.put("today_measles_2_date",submission.getFieldValue("measles_2_date_today")!=null ?submission.getFieldValue("measles_2_date_today"):"");
        map.put("today_measles_2_dose",submission.getFieldValue("measles_2_dose_today")!=null ?submission.getFieldValue("measles_2_dose_today"):"");


        //Other Details
        map.put("diseases_at_birth",submission.getFieldValue("existing_child_was_suffering_from_a_disease_at_birth")!=null ?submission.getFieldValue("existing_child_was_suffering_from_a_disease_at_birth"):"");

        map.put("side_effects",submission.getFieldValue("the_temporary_side-effects_of_immunization_shots_AEFI")!=null ?submission.getFieldValue("the_temporary_side-effects_of_immunization_shots_AEFI"):"");

        map.put("reminders_approval",submission.getFieldValue("existing_reminders_approval")!=null ?submission.getFieldValue("existing_reminders_approval"):"");
        map.put("contact_phone_number",submission.getFieldValue("existing_contact_phone_number") !=null ?submission.getFieldValue("existing_contact_phone_number") :"");



 //       String gps= submission.getFieldValue("center_gps");
      //  String retrovaccines=submission.getFieldValue("vaccines");
      //  String retrobcg=submission.getFieldValue("bcg");
//        String vaccines2=submission.getFieldValue("vaccines_2");
   //     String vaccination_date=submission.getFieldValue("vaccination_date");
    //    String disease_at_birth=submission.getFieldValue("existing_child_was_suffering_from_a_disease_at_birth");
    //    String vaccines=submission.getFieldValue("the_temporary_side-effects_of_immunization_shots_AEFI");
   //     String sideeffects=submission.getFieldValue("vaccines");
   //     String existing_reminders_approval=submission.getFieldValue("existing_reminders_approval");
    //    String existing_contact_phone_number=submission.getFieldValue("existing_contact_phone_number");
        //String vaccines=submission.getFieldValue("vaccines");
      //  Log.d("followup submission : ",gps);
     //   String detail =
        JSONObject json =new JSONObject(map);
        Log.d("followup submission : ",json.toString());

                allTimelines.add(new TimelineEvent(submission.entityId(), "CHILDFOLLOWUP", LocalDate.parse(submission.getFieldValue("vaccination_date")), "child followup", json.toString() ,null));
    }
}
