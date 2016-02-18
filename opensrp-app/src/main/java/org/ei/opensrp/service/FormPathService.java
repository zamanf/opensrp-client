package org.ei.opensrp.service;

import android.content.res.AssetManager;
import android.os.Environment;

import org.apache.commons.io.IOUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.application.OpenSRPApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by Dimas Ciputra on 3/22/15.
 */
public class FormPathService {

    public static String sdcardPath = Environment.getExternalStorageDirectory().getPath() +"/Download/OpenSRP/form/";
    public static String sdcardPathDownload = Environment.getExternalStorageDirectory().getPath() + "/Download/OpenSRP/zip/";
    public static String appPath = "www/form/";

    @Inject
    private AssetManager assetManager;

    public FormPathService() {
        OpenSRPApplication.getInstance().inject(this);
    }

    public FormPathService(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public String getForms(String file, String encoding) throws IOException {
        File formFile = new File(sdcardPath + file);

        if(formFile.exists()) {
            return IOUtils.toString(new FileInputStream(formFile), encoding);
        }

        return IOUtils.toString(this.assetManager.open(appPath + file), encoding);
    }

}