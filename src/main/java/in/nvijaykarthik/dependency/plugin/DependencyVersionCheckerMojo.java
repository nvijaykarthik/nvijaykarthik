package in.nvijaykarthik.dependency.plugin;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
// XML mapping classes for dependencyVersionPolicy.xml
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;

@Mojo(name = "check-latest", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class DependencyVersionCheckerMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${settings}", readonly = true)
    private Settings settings;
    /**
     * Comma separated list of groupIds to check. Only dependencies matching these will be checked.
     */
    @Parameter(property = "checkGroupIds", required = false)
    private String checkGroupIds;
    /**
     * URL to the XML metadata file with forced updates rules.
     */
    @Parameter(property = "versionPolicyXmlUrl", required = true)
    private String versionPolicyXmlUrl;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting Dependency Version Checker");

        // Parse comma separated groupIds to check into a set
        Set<String> groupIdFilter = (checkGroupIds == null || checkGroupIds.isEmpty()) ?
                null : Set.of(checkGroupIds.split("\\s*,\\s*"));

        // Load forced update metadata from remote XML
        DependencyVersionPolicy policy = loadVersionPolicy(versionPolicyXmlUrl);

        // Get artifacts - includes direct and transitive dependencies
        Set<Artifact> artifacts = project.getArtifacts();

        for (Artifact artifact : artifacts) {
            // Filter by groupId if specified
            if (groupIdFilter != null && !groupIdFilter.contains(artifact.getGroupId())) {
                continue;
            }

            String groupId = artifact.getGroupId();
            String artifactId = artifact.getArtifactId();
            String currentVersion = artifact.getVersion();

            // Check if this dependency is in forced update policy
            DependencyVersionPolicy.Dependency forcedDep = policy.findDependency(groupId, artifactId);

            if (forcedDep != null) {
                DefaultArtifactVersion current = new DefaultArtifactVersion(currentVersion);
                DefaultArtifactVersion safe = new DefaultArtifactVersion(forcedDep.getMinSafeVersion());

                if (current.compareTo(safe) < 0 && forcedDep.isForceUpdate()) {
                    throw new MojoFailureException(String.format(
                            "Dependency %s:%s version %s is below minimum safe version %s. %s",
                            groupId, artifactId, currentVersion, forcedDep.getMinSafeVersion(), forcedDep.getMessage()
                    ));
                } else if (current.compareTo(safe) < 0) {
                    getLog().warn(String.format("Dependency %s:%s version %s meets minimum safe version %s.",
                            groupId, artifactId, currentVersion, forcedDep.getMinSafeVersion()));
                }
            } else {
                // Here you could add logic for warnings or info about old versions
                getLog().debug(String.format("Dependency %s:%s version %s has no forced update rule defined.",
                        groupId, artifactId, currentVersion));
            }
        }

        getLog().info("Dependency Version Check completed successfully.");
    }

    private DependencyVersionPolicy loadVersionPolicy(String url) throws MojoExecutionException {
        try (InputStream is = new URL(url).openStream()) {
            JAXBContext jc = JAXBContext.newInstance(DependencyVersionPolicy.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            return (DependencyVersionPolicy) unmarshaller.unmarshal(is);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to load or parse version policy XML from " + url, e);
        }
    }

    @XmlRootElement(name = "dependencyVersionPolicy")
    public static class DependencyVersionPolicy {
        private List<Dependency> dependencies;

        @XmlElement(name = "dependency")
        public List<Dependency> getDependencies() {
            return dependencies;
        }

        public void setDependencies(List<Dependency> dependencies) {
            this.dependencies = dependencies;
        }

        public Dependency findDependency(String groupId, String artifactId) {
            if (dependencies == null) return null;
            for (Dependency d : dependencies) {
                if (d.getGroupId().equals(groupId) && d.getArtifactId().equals(artifactId)) {
                    return d;
                }
            }
            return null;
        }

        public static class Dependency {
            private String groupId;
            private String artifactId;
            private String minSafeVersion;
            private boolean forceUpdate;
            private String message;

            @XmlElement
            public String getGroupId() {
                return groupId;
            }

            public void setGroupId(String groupId) {
                this.groupId = groupId;
            }

            @XmlElement
            public String getArtifactId() {
                return artifactId;
            }

            public void setArtifactId(String artifactId) {
                this.artifactId = artifactId;
            }

            @XmlElement
            public String getMinSafeVersion() {
                return minSafeVersion;
            }

            public void setMinSafeVersion(String minSafeVersion) {
                this.minSafeVersion = minSafeVersion;
            }

            @XmlElement
            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public boolean isForceUpdate() {
                return forceUpdate;
            }

            @XmlElement
            public void setForceUpdate(boolean forceUpdate) {
                this.forceUpdate = forceUpdate;
            }
        }
    }
}
