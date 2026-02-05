import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;
//org.springframework.boot.env.EnvironmentPostProcessor=com.example.VaultLogicProcessor

public class VaultValidationPostProcessor implements EnvironmentPostProcessor, Ordered {

    // Run after config data is loaded so Vault is present
    @Override
    public int getOrder() {
        // Higher values = lower priority = runs later.
        // This ensures Vault (via ConfigData) has finished its work.
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 1. Read from Vault
        String dbPassword = environment.getProperty("my-vault-db-password");

        // 2. Ensure/Validate it exists
        if (dbPassword == null || dbPassword.isEmpty()) {
            // Option A: Log a warning
            System.err.println("CRITICAL: Vault properties not found!");
            // Option B: Throw exception to stop startup
            // throw new IllegalStateException("Vault properties are missing!");
        }

        // 3. Add dynamic properties based on Vault data
        Map<String, Object> additionalProps = new HashMap<>();
        if ("secret-val".equals(dbPassword)) {
            additionalProps.put("app.mode", "secure");
        } else {
            additionalProps.put("app.mode", "standard");
        }

        // 4. Inject into the Environment
        // addFirst makes these properties high priority
        environment.getPropertySources().addFirst(
            new MapPropertySource("myProgrammaticProps", additionalProps)
        );
    }
}
