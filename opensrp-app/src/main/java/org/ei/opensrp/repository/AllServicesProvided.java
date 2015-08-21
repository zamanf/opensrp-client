package org.ei.opensrp.repository;

import org.ei.opensrp.domain.ServiceProvided;
import org.ei.opensrp.repository.cloudant.ReportsModel;
import org.ei.opensrp.repository.cloudant.ServiceProvidedModel;

import java.util.List;

public class AllServicesProvided {
    private ServiceProvidedRepository repository;

    ServiceProvidedModel mServiceProvidedModel = org.ei.opensrp.Context.getInstance().serviceProvidedModel();

    public AllServicesProvided(ServiceProvidedRepository repository) {
        this.repository = repository;
    }

    public List<ServiceProvided> findByEntityIdAndServiceNames(String entityId, String... names) {
        return mServiceProvidedModel.findByEntityIdAndServiceNames(entityId, names);
    }

    public void add(ServiceProvided serviceProvided) {
        mServiceProvidedModel.add(serviceProvided);
    }
}
