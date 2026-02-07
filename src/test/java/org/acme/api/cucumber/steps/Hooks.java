package org.acme.api.cucumber.steps;

import io.cucumber.java.Before;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.acme.api.cucumber.VersionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class Hooks {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hooks.class);

    @Inject
    VersionContext versionContext;

    @Before("@v1")
    public void setV1() {
        LOGGER.info("Setting API Version to v1 based on tag");
        versionContext.setVersion("v1");
    }

    @Before("@v2")
    public void setV2() {
        LOGGER.info("Setting API Version to v2 based on tag");
        versionContext.setVersion("v2");
    }

    @Before("@v3")
    public void setV3() {
        LOGGER.info("Setting API Version to v3 based on tag");
        versionContext.setVersion("v3");
    }

    @Before("@v4")
    public void setV4() {
        LOGGER.info("Setting API Version to v4 based on tag");
        versionContext.setVersion("v4");
    }

    @Before("@v5")
    public void setV5() {
        LOGGER.info("Setting API Version to v5 based on tag");
        versionContext.setVersion("v5");
    }
}
