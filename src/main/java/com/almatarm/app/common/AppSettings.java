package com.almatarm.app.common;

import com.almatarm.lego.common.SystemUtil;
import com.almatarm.lego.json.JsonUtil;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.JSONConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.bouncycastle.asn1.ua.DSTU4145NamedCurves.params;
import static org.bouncycastle.asn1.x500.style.RFC4519Style.c;

/**
 * Created by almatarm on 28/10/2019.
 */
public class AppSettings {
    public static String SETTING_DIR = ".app-settings";

    String appName;
    private Configuration configuration;
    private FileBasedConfigurationBuilder<FileBasedConfiguration> builder;

    public AppSettings(String appName) {
        this.appName = appName;
    }

    public Configuration getAppConfiguration() {
        File configFile = getSettingsFile(appName);
        System.out.println("Using App Setting: " + configFile.getAbsolutePath());
        Parameters params = new Parameters();
        builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(JSONConfiguration.class)
                        .configure(params.properties()
                                .setFile(getSettingsFile(appName))
                                .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
        try {
            if(!configFile.exists()) JsonUtil.createEmptyJSONFile(configFile);
            configuration = builder.getConfiguration();
        } catch (ConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
        return configuration;
    }

    public void save() {
        try {
            builder.save();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static File getSettingsFile(String appName) {
        File settingsDir = new File(System.getProperty("user.home"), SETTING_DIR);
        settingsDir.mkdirs();

        List<String> gdResolustions = new ArrayList<String>();
        GraphicsDevice[] gds = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        for(GraphicsDevice gd : gds) {
            int width = gd.getDisplayMode().getWidth();
            int height = gd.getDisplayMode().getHeight();
            gdResolustions.add(String.format("%dx%d", width, height));
        }


        String name = String.format("%s - %s - %s - %s.json",
                appName,
                SystemUtil.getHostName(),
                SystemUtil.getOperationSystem().toString(),
                gdResolustions.toString());
        return new File(settingsDir, name);
    }

    public static void main(String[] args) {
        AppSettings settings = new AppSettings("AppSettings");
        Configuration config = settings.getAppConfiguration();
        config.addProperty("Int", 123);
        config.addProperty("String", "Hello");
        settings.save();

        System.out.println(config.getInt("Int"));
        System.out.println(config.getString("String"));
    }
}
