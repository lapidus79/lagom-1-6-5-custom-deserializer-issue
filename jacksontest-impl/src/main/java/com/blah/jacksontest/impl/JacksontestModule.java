package com.blah.jacksontest.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

import com.blah.jacksontest.api.JacksontestService;

/**
 * The module that binds the JacksontestService so that it can be served.
 */
public class JacksontestModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bindService(JacksontestService.class, JacksontestServiceImpl.class);
    }
}
