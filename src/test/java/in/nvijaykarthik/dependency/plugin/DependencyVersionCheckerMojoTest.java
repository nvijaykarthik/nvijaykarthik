package in.nvijaykarthik.dependency.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DependencyVersionCheckerMojoTest {

    @Mock
    private MavenProject project;

    @Mock
    private Log log;

    private DependencyVersionCheckerMojo mojo;

    @BeforeEach
    void setUp() {
        mojo = new DependencyVersionCheckerMojo();
        mojo.setLog(log);
        // project is injected per-test where needed
    }

    @Test
    void loadVersionPolicy_parsesClasspathXml() throws Exception {
        Method m = DependencyVersionCheckerMojo.class.getDeclaredMethod("loadVersionPolicy", String.class);
        m.setAccessible(true);

        String resource = getClass().getClassLoader().getResource("dependencyVersionPolicy.xml").toString();
        Object policyObj = m.invoke(mojo, resource);
        assertNotNull(policyObj);
        DependencyVersionCheckerMojo.DependencyVersionPolicy policy = (DependencyVersionCheckerMojo.DependencyVersionPolicy) policyObj;
        assertNotNull(policy.getDependencies());
        assertFalse(policy.getDependencies().isEmpty());

        DependencyVersionCheckerMojo.DependencyVersionPolicy.Dependency dep = policy.getDependencies().get(0);
        assertEquals("com.example", dep.getGroupId());
        assertEquals("example-artifact", dep.getArtifactId());
        assertEquals("2.1.0", dep.getMinSafeVersion());
    }

    @Test
    void loadVersionPolicy_invalidUrl_throwsMojoExecutionException() throws Exception {
        Method m = DependencyVersionCheckerMojo.class.getDeclaredMethod("loadVersionPolicy", String.class);
        m.setAccessible(true);

        try {
            m.invoke(mojo, "file:///nonexistent/does-not-exist.xml");
            fail("Expected an exception");
        } catch (Exception e) {
            Throwable cause = e.getCause();
            assertNotNull(cause);
            assertTrue(cause instanceof MojoExecutionException);
        }
    }

    @Test
    void findPolicyUrlFromRepositories_picksReleaseVersionFromMetadata() throws Exception {
        // Create temp repo structure
        Path base = Files.createTempDirectory("repo");
        base.toFile().deleteOnExit();

        String group = "com.example.metadata";
        String artifactId = "dependencyVersionPolicy";
        String version = "2.1.0";

        Path groupPath = base.resolve(group.replace('.', '/')).resolve(artifactId);
        Files.createDirectories(groupPath.resolve(version));

        // write maven-metadata.xml with <release>
        String metadata = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<metadata>\n" +
                "  <groupId>" + group + "</groupId>\n" +
                "  <artifactId>" + artifactId + "</artifactId>\n" +
                "  <versioning>\n" +
                "    <release>" + version + "</release>\n" +
                "  </versioning>\n" +
                "</metadata>\n";
        Files.writeString(groupPath.resolve("maven-metadata.xml"), metadata, StandardCharsets.UTF_8);

        // write candidate policy xml
        String policyXml = "<?xml version=\"1.0\"?><dependencyVersionPolicy/>";
        Files.writeString(groupPath.resolve(version).resolve(artifactId + "-" + version + ".xml"), policyXml, StandardCharsets.UTF_8);

        // mock repository and project
        ArtifactRepository repo = mock(ArtifactRepository.class);
        when(repo.getUrl()).thenReturn(base.toUri().toString());
        when(project.getRemoteArtifactRepositories()).thenReturn(Arrays.asList(repo));
        setField(mojo, "project", project);

        setField(mojo, "versionPolicyGroup", group);
        setField(mojo, "versionPolicyArtifactId", artifactId);

        String result = invokeFindPolicyUrl(mojo);
        assertNotNull(result);
        assertTrue(result.contains(version));
    }

    @Test
    void execute_throwsWhenForcedUpdateBelowSafeVersion() throws Exception {
        // create repo + metadata + candidate that contains a forced dependency
        Path base = Files.createTempDirectory("repo2");
        base.toFile().deleteOnExit();

        String group = "com.example";
        String artifactId = "dependencyVersionPolicy";
        String version = "1.0.0"; // chosen version for candidate file

        Path groupPath = base.resolve(group.replace('.', '/')).resolve(artifactId);
        Files.createDirectories(groupPath.resolve(version));

        String metadata = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<metadata>\n" +
                "  <groupId>" + group + "</groupId>\n" +
                "  <artifactId>" + artifactId + "</artifactId>\n" +
                "  <versioning>\n" +
                "    <release>" + version + "</release>\n" +
                "  </versioning>\n" +
                "</metadata>\n";
        Files.writeString(groupPath.resolve("maven-metadata.xml"), metadata, StandardCharsets.UTF_8);

        // Candidate policy contains forced update for com.example:lib with minSafeVersion 2.0.0
        String candidateXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<dependencyVersionPolicy>\n" +
                "  <dependency>\n" +
                "    <groupId>com.example</groupId>\n" +
                "    <artifactId>lib</artifactId>\n" +
                "    <minSafeVersion>2.0.0</minSafeVersion>\n" +
                "    <forceUpdate>true</forceUpdate>\n" +
                "    <message>Security</message>\n" +
                "  </dependency>\n" +
                "</dependencyVersionPolicy>\n";
        Files.writeString(groupPath.resolve(version).resolve(artifactId + "-" + version + ".xml"), candidateXml, StandardCharsets.UTF_8);

        // mock repo/project
        ArtifactRepository repo = mock(ArtifactRepository.class);
        when(repo.getUrl()).thenReturn(base.toUri().toString());
        when(project.getRemoteArtifactRepositories()).thenReturn(Arrays.asList(repo));

        // artifact present in project is com.example:lib:1.0.0
        Artifact dep = mock(Artifact.class);
        when(dep.getGroupId()).thenReturn("com.example");
        when(dep.getArtifactId()).thenReturn("lib");
        when(dep.getVersion()).thenReturn("1.0.0");
        Set<Artifact> arts = new HashSet<>();
        arts.add(dep);
        when(project.getArtifacts()).thenReturn(arts);

        setField(mojo, "project", project);
        setField(mojo, "versionPolicyGroup", group);
        setField(mojo, "versionPolicyArtifactId", artifactId);

        // execute should throw MojoFailureException because forced update required and current version < safe
        assertThrows(MojoFailureException.class, () -> mojo.execute());
    }

    @Test
    void execute_respectsCheckGroupIds_filtering() throws Exception {
        // create a simple policy that would force an update for com.other, but project artifact is com.example and checkGroupIds filters to com.example
        Path tmp = Files.createTempDirectory("repo3");
        tmp.toFile().deleteOnExit();
        String group = "com.example";
        String artifactId = "dependencyVersionPolicy";
        String version = "1.0.0";
        Path groupPath = tmp.resolve(group.replace('.', '/')).resolve(artifactId);
        Files.createDirectories(groupPath.resolve(version));

        String metadata = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<metadata>\n" +
                "  <groupId>" + group + "</groupId>\n" +
                "  <artifactId>" + artifactId + "</artifactId>\n" +
                "  <versioning>\n" +
                "    <release>" + version + "</release>\n" +
                "  </versioning>\n" +
                "</metadata>\n";
        Files.writeString(groupPath.resolve("maven-metadata.xml"), metadata, StandardCharsets.UTF_8);

        String candidateXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<dependencyVersionPolicy>\n" +
                "  <dependency>\n" +
                "    <groupId>com.other</groupId>\n" +
                "    <artifactId>lib</artifactId>\n" +
                "    <minSafeVersion>2.0.0</minSafeVersion>\n" +
                "    <forceUpdate>true</forceUpdate>\n" +
                "  </dependency>\n" +
                "</dependencyVersionPolicy>\n";
        Files.writeString(groupPath.resolve(version).resolve(artifactId + "-" + version + ".xml"), candidateXml, StandardCharsets.UTF_8);

        ArtifactRepository repo = mock(ArtifactRepository.class);
        when(repo.getUrl()).thenReturn(tmp.toUri().toString());
        when(project.getRemoteArtifactRepositories()).thenReturn(Arrays.asList(repo));

        Artifact artifact = mock(Artifact.class);
        when(artifact.getGroupId()).thenReturn("com.example");
        when(artifact.getArtifactId()).thenReturn("lib");
        when(artifact.getVersion()).thenReturn("1.0.0");
        Set<Artifact> arts = new HashSet<>();
        arts.add(artifact);
        when(project.getArtifacts()).thenReturn(arts);

        setField(mojo, "project", project);
        setField(mojo, "versionPolicyGroup", group);
        setField(mojo, "versionPolicyArtifactId", artifactId);
        setField(mojo, "checkGroupIds", "com.example");

        // Should not throw because policy targets com.other and we only check com.example
        mojo.execute();
    }

    @Test
    void findPolicyUrlFromRepositories_returnsNullWhenMetadataInvalid() throws Exception {
        Path base = Files.createTempDirectory("repo-invalid");
        base.toFile().deleteOnExit();
        String group = "com.example.metadata";
        String artifactId = "dependencyVersionPolicy";
        Path groupPath = base.resolve(group.replace('.', '/')).resolve(artifactId);
        Files.createDirectories(groupPath);
        Files.writeString(groupPath.resolve("maven-metadata.xml"), "<invalid>not xml</invalid>", StandardCharsets.UTF_8);

        ArtifactRepository repo = mock(ArtifactRepository.class);
        when(repo.getUrl()).thenReturn(base.toUri().toString());
        when(project.getRemoteArtifactRepositories()).thenReturn(Arrays.asList(repo));

        setField(mojo, "project", project);
        setField(mojo, "versionPolicyGroup", group);
        setField(mojo, "versionPolicyArtifactId", artifactId);

        String result = invokeFindPolicyUrl(mojo);
        assertNull(result, "Invalid metadata should result in no candidate URL and null return");
    }

    @Test
    void findPolicyUrlFromRepositories_returnsNullWhenNoRepositories() throws Exception {
        setField(mojo, "project", project);
        when(project.getRemoteArtifactRepositories()).thenReturn(null);

        String result = invokeFindPolicyUrl(mojo);
        assertNull(result, "No repositories configured should return null");
    }

    @Test
    void loadVersionPolicy_malformedXml_throwsMojoExecutionException() throws Exception {
        Method m = DependencyVersionCheckerMojo.class.getDeclaredMethod("loadVersionPolicy", String.class);
        m.setAccessible(true);

        Path tmp = Files.createTempFile("bad-policy", ".xml");
        Files.writeString(tmp, "<not-xml>", StandardCharsets.UTF_8);

        try {
            m.invoke(mojo, tmp.toUri().toString());
            fail("Expected exception when parsing malformed XML");
        } catch (Exception e) {
            Throwable cause = e.getCause();
            assertNotNull(cause);
            assertTrue(cause instanceof MojoExecutionException);
        }
    }

    @Test
    void execute_warnsWhenBelowSafeButNotForced() throws Exception {
        // repo + metadata + candidate where forceUpdate is false
        Path base = Files.createTempDirectory("repo-warn");
        base.toFile().deleteOnExit();

        String group = "com.example";
        String artifactId = "dependencyVersionPolicy";
        String version = "1.0.0";
        Path groupPath = base.resolve(group.replace('.', '/')).resolve(artifactId);
        Files.createDirectories(groupPath.resolve(version));

        String metadata = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<metadata>\n" +
                "  <groupId>" + group + "</groupId>\n" +
                "  <artifactId>" + artifactId + "</artifactId>\n" +
                "  <versioning>\n" +
                "    <release>" + version + "</release>\n" +
                "  </versioning>\n" +
                "</metadata>\n";
        Files.writeString(groupPath.resolve("maven-metadata.xml"), metadata, StandardCharsets.UTF_8);

        String candidateXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<dependencyVersionPolicy>\n" +
                "  <dependency>\n" +
                "    <groupId>com.example</groupId>\n" +
                "    <artifactId>lib</artifactId>\n" +
                "    <minSafeVersion>2.0.0</minSafeVersion>\n" +
                "    <forceUpdate>false</forceUpdate>\n" +
                "  </dependency>\n" +
                "</dependencyVersionPolicy>\n";
        Files.writeString(groupPath.resolve(version).resolve(artifactId + "-" + version + ".xml"), candidateXml, StandardCharsets.UTF_8);

        ArtifactRepository repo = mock(ArtifactRepository.class);
        when(repo.getUrl()).thenReturn(base.toUri().toString());
        when(project.getRemoteArtifactRepositories()).thenReturn(Arrays.asList(repo));

        Artifact dep = mock(Artifact.class);
        when(dep.getGroupId()).thenReturn("com.example");
        when(dep.getArtifactId()).thenReturn("lib");
        when(dep.getVersion()).thenReturn("1.0.0");
        Set<Artifact> arts = new HashSet<>();
        arts.add(dep);
        when(project.getArtifacts()).thenReturn(arts);

        // inject
        setField(mojo, "project", project);
        setField(mojo, "versionPolicyGroup", group);
        setField(mojo, "versionPolicyArtifactId", artifactId);

        // should not throw; should log a warning
        mojo.execute();
        verify(log).warn(contains("Dependency com.example:lib"));
    }

    @Test
    void dependencyFindDependency_nullAndPresent() {
        DependencyVersionCheckerMojo.DependencyVersionPolicy policy = new DependencyVersionCheckerMojo.DependencyVersionPolicy();
        assertNull(policy.findDependency("x", "y"));

        DependencyVersionCheckerMojo.DependencyVersionPolicy.Dependency d = new DependencyVersionCheckerMojo.DependencyVersionPolicy.Dependency();
        d.setGroupId("g");
        d.setArtifactId("a");
        d.setMinSafeVersion("1.2.3");
        policy.setDependencies(Arrays.asList(d));

        assertNotNull(policy.findDependency("g", "a"));
    }

    // ----------------- Helpers -----------------
    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String invokeFindPolicyUrl(DependencyVersionCheckerMojo mojo) {
        try {
            Method m = DependencyVersionCheckerMojo.class.getDeclaredMethod("findPolicyUrlFromRepositories");
            m.setAccessible(true);
            return (String) m.invoke(mojo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
