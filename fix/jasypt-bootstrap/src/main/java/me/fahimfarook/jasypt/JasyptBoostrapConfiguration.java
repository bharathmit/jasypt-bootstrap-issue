package me.fahimfarook.jasypt;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import com.ulisesbocchio.jasyptspringboot.EnableEncryptablePropertySourcesPostProcessor;
import com.ulisesbocchio.jasyptspringboot.InterceptionMode;
import com.ulisesbocchio.jasyptspringboot.configuration.EnableEncryptablePropertySourcesConfiguration;

/**
 * Workaround for config-server boostraping early - i.e. without decrypting
 * encrypted passwords.
 * 
 * @author Fahim Farook
 *
 */
@Import(EnableEncryptablePropertySourcesConfiguration.class)
@ConditionalOnProperty("spring.cloud.config.server.bootstrap")
@Configuration
public class JasyptBoostrapConfiguration {
	
	@Bean
	public static EnableEncryptablePropertySourcesPostProcessor enableEncryptablePropertySourcesPostProcessor(
			ConfigurableEnvironment environment) {
		final boolean proxyPropertySources = 
				environment.getProperty("jasypt.encryptor.proxyPropertySources", Boolean.TYPE, false);
		final InterceptionMode interceptionMode = 
				proxyPropertySources ? InterceptionMode.PROXY : InterceptionMode.WRAPPER;
		return new HighPrecedenceEncryptablePropertySourcesPostProcessor(environment, interceptionMode);
	}

	static class HighPrecedenceEncryptablePropertySourcesPostProcessor
			extends EnableEncryptablePropertySourcesPostProcessor implements Ordered {

		public static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 15;

		private int order = ORDER;

		public HighPrecedenceEncryptablePropertySourcesPostProcessor(
				final ConfigurableEnvironment environment, final InterceptionMode interceptionMode) {
			super(environment, interceptionMode);
		}

		@Override
		public int getOrder() {
			return this.order;
		}
	}

}