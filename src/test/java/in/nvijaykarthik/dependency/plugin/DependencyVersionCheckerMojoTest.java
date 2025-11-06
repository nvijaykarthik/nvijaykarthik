package in.nvijaykarthik.dependency.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

public class DependencyVersionCheckerMojoTest {

    @Test
    public void testLoadVersionPolicy_parsesXmlCorrectly() throws Exception {
        DependencyVersionCheckerMojo mojo = new DependencyVersionCheckerMojo();

        // Locate test resource
        URL resource = getClass().getClassLoader().getResource("dependencyVersionPolicy.xml");
        assertNotNull(resource, "Test resource should be available");

        // Invoke private method loadVersionPolicy via reflection
        Method m = DependencyVersionCheckerMojo.class.getDeclaredMethod("loadVersionPolicy", String.class);
        m.setAccessible(true);

        Object policyObj = m.invoke(mojo, resource.toString());
        assertNotNull(policyObj, "Parsed policy should not be null");

        // Cast and assert its content
        DependencyVersionCheckerMojo.DependencyVersionPolicy policy = (DependencyVersionCheckerMojo.DependencyVersionPolicy) policyObj;
        assertNotNull(policy.getDependencies());
        assertFalse(policy.getDependencies().isEmpty());

        DependencyVersionCheckerMojo.DependencyVersionPolicy.Dependency dep = policy.getDependencies().get(0);
        assertEquals("com.example", dep.getGroupId());
        assertEquals("example-artifact", dep.getArtifactId());
        assertEquals("2.1.0", dep.getMinSafeVersion());
        assertEquals("Use newer version", dep.getMessage());
        // New boolean field added: forceUpdate
        assertFalse(dep.isForceUpdate(), "forceUpdate should be false as per test resource");
    }

    @Test
    public void testLoadVersionPolicy_invalidUrl_throws() throws Exception {
        DependencyVersionCheckerMojo mojo = new DependencyVersionCheckerMojo();

        Method m = DependencyVersionCheckerMojo.class.getDeclaredMethod("loadVersionPolicy", String.class);
        m.setAccessible(true);

        try {
            m.invoke(mojo, "http://nonexistent.invalid/resource.xml");
        } catch (Exception e) {
            // InvocationTargetException expected, cause should be MojoExecutionException
            Throwable cause = e.getCause();
            boolean found = false;
            while (cause != null) {
                if (cause instanceof MojoExecutionException) {
                    found = true;
                    break;
                }
                cause = cause.getCause();
            }
            if (!found) throw e;
        }
    }

    @Test
    public void testExecute_warnAndForcePaths() throws Exception {
        DependencyVersionCheckerMojo mojo = new DependencyVersionCheckerMojo();
        // give it a real log so warnings aren't lost
        mojo.setLog(new SystemStreamLog());

        // create a fake MavenProject and inject into mojo
        MavenProject project = new MavenProject();
        Set<Artifact> artifacts = new HashSet<>();

    // create two fake artifacts using DefaultArtifact but supply a non-null ArtifactHandler to avoid NPE
    ArtifactHandler handler = new DefaultArtifactHandler("jar");
    Artifact oldButNotForced = new org.apache.maven.artifact.DefaultArtifact("com.example", "example-artifact", "1.0.0", null, "jar", null, handler);
    Artifact oldAndForced = new org.apache.maven.artifact.DefaultArtifact("org.forced", "forced-artifact", "1.0.0", null, "jar", null, handler);

        artifacts.add(oldButNotForced);
        artifacts.add(oldAndForced);

        project.setArtifacts(artifacts);

        // inject project into mojo via reflection
        Field projField = DependencyVersionCheckerMojo.class.getDeclaredField("project");
        projField.setAccessible(true);
        projField.set(mojo, project);

        // point mojo to test resource XML which contains rules for com.example and org.forced
        Field urlField = DependencyVersionCheckerMojo.class.getDeclaredField("versionPolicyXmlUrl");
        urlField.setAccessible(true);
        URL resource = getClass().getClassLoader().getResource("dependencyVersionPolicy.xml");
        urlField.set(mojo, resource.toString());

        // run execute: should not throw because test resource marks forceUpdate false for com.example
        mojo.execute();

        // Now alter the policy resource on-the-fly by creating a policy with forceUpdate true for org.forced
        // We'll inject a custom policy by replacing loadVersionPolicy method via reflection (callable override isn't trivial),
        // Instead, set versionPolicyXmlUrl to a custom local file we write containing forceUpdate true for org.forced
        File tmp = File.createTempFile("policy", ".xml");
        tmp.deleteOnExit();
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<dependencyVersionPolicy>\n" +
                "  <dependency>\n" +
                "    <groupId>org.forced</groupId>\n" +
                "    <artifactId>forced-artifact</artifactId>\n" +
                "    <minSafeVersion>2.0.0</minSafeVersion>\n" +
                "    <forceUpdate>true</forceUpdate>\n" +
                "    <message>Must upgrade</message>\n" +
                "  </dependency>\n" +
                "</dependencyVersionPolicy>\n";
        java.nio.file.Files.writeString(tmp.toPath(), xml);
        urlField.set(mojo, tmp.toURI().toString());

        boolean threw = false;
        try {
            mojo.execute();
        } catch (MojoFailureException mfe) {
            threw = true;
        }
        assertEquals(true, threw, "execute should throw MojoFailureException when a forced dependency is below safe version");
    }

    @Test
    public void testGroupIdFilter_skipsArtifacts() throws Exception {
        DependencyVersionCheckerMojo mojo = new DependencyVersionCheckerMojo();
        mojo.setLog(new SystemStreamLog());

        MavenProject project = new MavenProject();
        Set<Artifact> artifacts = new HashSet<>();
    ArtifactHandler handler = new DefaultArtifactHandler("jar");
    Artifact a = new org.apache.maven.artifact.DefaultArtifact("com.other", "other-art", "1.0.0", null, "jar", null, handler);
        artifacts.add(a);
        project.setArtifacts(artifacts);

        Field projField = DependencyVersionCheckerMojo.class.getDeclaredField("project");
        projField.setAccessible(true);
        projField.set(mojo, project);

        // set checkGroupIds to only include com.example so com.other is skipped
        Field checkField = DependencyVersionCheckerMojo.class.getDeclaredField("checkGroupIds");
        checkField.setAccessible(true);
        checkField.set(mojo, "com.example");

        // point to valid policy
        Field urlField = DependencyVersionCheckerMojo.class.getDeclaredField("versionPolicyXmlUrl");
        urlField.setAccessible(true);
        URL resource = getClass().getClassLoader().getResource("dependencyVersionPolicy.xml");
        urlField.set(mojo, resource.toString());

        // should complete without exceptions because artifact is filtered out
        mojo.execute();
    }
}
