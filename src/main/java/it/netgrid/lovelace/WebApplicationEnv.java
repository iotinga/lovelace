package it.netgrid.lovelace;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.HashMap;
import java.util.Map;

public class WebApplicationEnv extends GuiceServletContextListener {

    private static Injector defaultInjector;

    @Override
    protected Injector getInjector() {
        return getDefaultInjector();
    }

    public static Injector getDefaultInjector() {
        if(defaultInjector == null) {
            defaultInjector = Main.getDefaultInjector();
        }
        return defaultInjector;
    }

}
