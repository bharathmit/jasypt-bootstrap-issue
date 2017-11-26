package me.fahimfarook.jasypt;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.ulisesbocchio.jasyptspringboot.configuration.EnableEncryptablePropertiesConfiguration;

/**
 * Workaround for config-server boostraping early - i.e. without decrypting
 * encrypted passwords.
 * 
 * @author Fahim Farook
 *
 */
@Configuration
@ConditionalOnClass(name = "org.springframework.cloud.bootstrap.BootstrapApplicationListener")
@ConditionalOnProperty(name = "spring.cloud.bootstrap.enabled", havingValue = "true", matchIfMissing = true)
public class JasyptSpringCloudBootstrapConfiguration {

	@Configuration
	@ConditionalOnProperty(name = "jasypt.encryptor.bootstrap", havingValue = "true", matchIfMissing = true)
	@Import(EnableEncryptablePropertiesConfiguration.class)
	protected static class BootstrappingEncryptablePropertiesConfiguration {

	}
}