package org.ei.opensrp.domain.form;

import com.cloudant.sync.datastore.BasicDocumentRevision;
import com.google.gson.Gson;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.ei.opensrp.domain.SyncStatus;

import java.util.HashMap;
import java.util.Map;

import static org.ei.opensrp.domain.SyncStatus.SYNCED;
import static org.ei.opensrp.domain.SyncStatus.PENDING;

public class FormSubmission {
    private String instanceId;
    private String entityId;
    private String formName;
    private String instance;
    private String clientVersion;
    private String formDataDefinitionVersion;
    private String serverVersion;
    private SyncStatus syncStatus;
    private FormInstance formInstance;

    // this is the revision in the database representing this task
    private BasicDocumentRevision revision;
    public BasicDocumentRevision getDocumentRevision() {
        return revision;
    }



    public FormSubmission(){
    }

    public FormSubmission(String instanceId, String entityId, String formName, String instance, String clientVersion, SyncStatus syncStatus, String formDataDefinitionVersion) {
        this(instanceId, entityId, formName, instance, clientVersion, syncStatus, formDataDefinitionVersion, null);
    }

    public FormSubmission(String instanceId, String entityId, String formName, String instance, String clientVersion, SyncStatus syncStatus, String formDataDefinitionVersion,
                          String serverVersion) {
        this.instanceId = instanceId;
        this.entityId = entityId;
        this.formName = formName;
        this.instance = instance;
        this.clientVersion = clientVersion;
        this.syncStatus = syncStatus;
        this.formDataDefinitionVersion = formDataDefinitionVersion;
        this.serverVersion = serverVersion;
        this.formInstance = new Gson().fromJson(instance, FormInstance.class);
    }

    public String instanceId() {
        return instanceId;
    }

    public String entityId() {
        return entityId;
    }

    public String formName() {
        return formName;
    }

    public String instance() {
        return instance;
    }

    public String version() {
        return clientVersion;
    }

    public String serverVersion() {
        return serverVersion;
    }

    public SyncStatus syncStatus() {
        return syncStatus;
    }

    public String formDataDefinitionVersion() {
        return formDataDefinitionVersion;
    }

    public void setFormDataDefinitionVersion(String formDataDefinitionVersion) {
        this.formDataDefinitionVersion = formDataDefinitionVersion;
    }

    public FormSubmission setSyncStatus(SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
        return this;
    }

    public FormSubmission setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
        return this;
    }

    public String getFieldValue(String fieldName) {
        return formInstance.getFieldValue(fieldName);
    }

    public SubForm getSubFormByName(String name) {
        return formInstance.getSubFormByName(name);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, "clientVersion");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "clientVersion");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Map<String, Object> asMap() {
        //String jsonString = new Gson().toJson(this);
        Map<String,Object> props = new HashMap<String,Object>();
        props.put("instanceId", instanceId);
        props.put("entityId", entityId);
        props.put("formName", formName);
        props.put("instance", instance);
        props.put("version", clientVersion);
        props.put("syncStatus", syncStatus);
        props.put("formDataDefinitionVersion", formDataDefinitionVersion);
        props.put("serverVersion", serverVersion);
        return props;
    }

    public static FormSubmission fromRevision(BasicDocumentRevision revision) {
        // this could also be done by a fancy object mapper
        Map<String, Object> map = revision.asMap();
        if (map.containsKey("instanceId") && map.containsKey("entityId") && map.containsKey("formName") && map.containsKey("instance") &&
                map.containsKey("version") && map.containsKey("syncStatus") && map.containsKey("formDataDefinitionVersion") ){
            String instanceId = (String) map.get("instanceId");
            String entityId =   (String) map.get("entityId");
            String formName =  (String) map.get("formName");
            String instance =  (String) map.get("instance");
            String clientVersion =  String.valueOf(map.get("version"));
            String syncStatusString =  (String) map.get("syncStatus");
            SyncStatus syncStatus = syncStatusString.equalsIgnoreCase("SYNCED") ? SYNCED : PENDING;
            String formDataDefinitionVersion =  (String) map.get("formDataDefinitionVersion");
            String serverVersion =  map.containsKey("serverVersion") ? (String) map.get("serverVersion") : null;

            FormSubmission formSubmission = new FormSubmission(instanceId, entityId, formName, instance, clientVersion, syncStatus, formDataDefinitionVersion, serverVersion);
            formSubmission.revision = revision;
            return formSubmission;
        }
        return null;
    }
}
