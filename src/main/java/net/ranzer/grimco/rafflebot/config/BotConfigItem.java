package net.ranzer.grimco.rafflebot.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface BotConfigItem {

	String key();
	Class<?> type();
	String _default();
}
