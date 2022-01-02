
import jplugman.api.Plugin;
import perobobbot.blague.JPlugin;

module perobobbot.blague {
    requires static lombok;
    requires java.desktop;

    requires org.apache.logging.log4j;

    requires jplugman.api;
    requires com.google.common;

    requires reactor.core;
    requires spring.webflux;

    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    requires perobobbot.plugin;
    requires perobobbot.extension;
    requires perobobbot.access;
    requires perobobbot.command;
    requires perobobbot.eventsub;
    requires perobobbot.lang;
    requires perobobbot.oauth;
    requires perobobbot.http;
    requires perobobbot.chat.core;
    requires perobobbot.twitch.client.api;
    requires perobobbot.messaging;
    requires perobobbot.data.service;

    opens perobobbot.blague.api to com.fasterxml.jackson.databind;

    provides Plugin with JPlugin;
}
