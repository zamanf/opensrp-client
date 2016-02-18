package org.ei.opensrp.service;

import org.ei.opensrp.application.OpenSRPApplication;
import org.ei.opensrp.db.adapters.ServiceProvidedRepository;
import org.ei.opensrp.domain.ServiceProvided;

import java.util.List;

import javax.inject.Inject;

public class ServiceProvidedService {
    @Inject
    private ServiceProvidedRepository allServiceProvided;

    public ServiceProvidedService() {
        OpenSRPApplication.getInstance().inject(this);
    }

    public List<ServiceProvided> findByEntityIdAndServiceNames(String entityId, String... names) {
        return allServiceProvided.findByEntityIdAndServiceNames(entityId, names);
    }

    public void add(ServiceProvided serviceProvided) {
        allServiceProvided.add(serviceProvided);
    }
}
