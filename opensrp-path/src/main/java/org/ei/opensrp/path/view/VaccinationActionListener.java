package org.ei.opensrp.path.view;

import org.ei.opensrp.path.domain.VaccineWrapper;

/**
 * Created by keyman on 22/11/2016.
 */
public interface VaccinationActionListener {

    public void onVaccinateToday(VaccineWrapper tag);

    public void onVaccinateEarlier(VaccineWrapper tag);

    public void onUndoVaccination(VaccineWrapper tag);
}
