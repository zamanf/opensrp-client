package org.ei.opensrp.domain;

import com.cloudant.sync.datastore.BasicDocumentRevision;

import java.util.HashMap;
import java.util.Map;

import static org.ei.opensrp.domain.SyncStatus.PENDING;
import static org.ei.opensrp.domain.SyncStatus.SYNCED;

/**
 * Created by Dimas Ciputra on 3/23/15.
 */
public class FormDefinitionVersion {

    private String formName;
    private String formDataDefinitionVersion;
    private String formDirName;
    private SyncStatus syncStatus;
    private String entityId;

    // this is the revision in the database representing this task
    private BasicDocumentRevision rev;
    public BasicDocumentRevision getDocumentRevision() {
        return rev;
    }

    public FormDefinitionVersion(String formName, String formDirName, String formDataDefinitionVersion) {
        this.formName = formName;
        this.formDirName = formDirName;
        this.formDataDefinitionVersion = formDataDefinitionVersion;
    }

    public FormDefinitionVersion withSyncStatus(SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
        return this;
    }

    public FormDefinitionVersion withFormId(String id) {
        this.entityId = id;
        return this;
    }

    public String getFormName() {
        return formName;
    }

    public String getVersion() {
        return formDataDefinitionVersion;
    }

    public String getFormDirName() { return formDirName; }

    public SyncStatus getSyncStatus() {
        return syncStatus;
    }

    public String getEntityId() { return entityId; }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public void setVersion(String formDataDefinitionVersion) {
        this.formDataDefinitionVersion = formDataDefinitionVersion;
    }

    public void setFormDirName(String formDirName) {
        this.formDirName = formDirName;
    }

    public void setSyncStatus(SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }

    public Map<String, Object> asMap() {
        //String jsonString = new Gson().toJson(this);
        Map<String,Object> props = new HashMap<String,Object>();
        props.put("formName", formName);
        props.put("formDataDefinitionVersion", formDataDefinitionVersion);
        props.put("formDirName", formDirName);
        props.put("syncStatus", syncStatus);
        props.put("entityId", entityId);
        props.put("syncStatus", syncStatus);
        return props;
    }

    public static FormDefinitionVersion fromRevision(BasicDocumentRevision rev) {
        Map<String, Object> map = rev.asMap();
        if(map.containsKey("formName") && map.containsKey("formDataDefinitionVersion") && map.containsKey("formDirName") && map.containsKey("syncStatus")) {
            String formName = (String) map.get("formName");
            String formDataDefinitionVersion = (String) map.get("formDataDefinitionVersion");
            String formDirName = (String) map.get("formDirName");
            String entityId = map.containsKey("entityId") ? (String) map.get("entityId") : null;
            String syncStatus = map.containsKey("syncStatus") ? (String) map.get("syncStatus") : null;
            FormDefinitionVersion formDefinitionVersion = new FormDefinitionVersion(formName, formDirName, formDataDefinitionVersion);
            return formDefinitionVersion;
        }
        return null;
    }
}
