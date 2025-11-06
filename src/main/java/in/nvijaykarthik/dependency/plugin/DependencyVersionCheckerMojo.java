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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

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
     * The policy XML is resolved from the project's configured remote
     * repositories (as defined in the POM or settings.xml). The mojo will
     * look for the file at: ${repoUrl}/in/nvijaykarthik/dependency/policy/dependencyVersionPolicy.xml
     */
    @Parameter(property = "versionPolicyUrl", required = false) 
     private String versionPolicyUrl;

    /**
     * GroupId under which the policy file is hosted in the remote repository.
     * This will be converted to a path (dots -> slashes) and joined with the
     * artifact name below when searching repositories.
     */
    @Parameter(property = "versionPolicyGroup", defaultValue = "in.nvijaykarthik.dependency.policy")
    private String versionPolicyGroup;


    /**
     * ArtifactId under which the policy file is hosted in the remote repository.
     * If present the mojo will attempt to read maven-metadata.xml and pick the
     * latest version of the artifact, then download the artifact file named
     * <artifactId>-<version>.xml from the repository. This allows the plugin
     * to always use the latest deployed policy version.
     */
    @Parameter(property = "versionPolicyArtifactId", required = false,defaultValue = "dependencyVersionPolicy")
    private String versionPolicyArtifactId;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting Dependency Version Checker");

        // Parse comma separated groupIds to check into a set
        Set<String> groupIdFilter = (checkGroupIds == null || checkGroupIds.isEmpty()) ?
                null : Set.of(checkGroupIds.split("\\s*,\\s*"));

        // Resolve the policy XML from configured remote repositories. The
        // plugin does not accept an external URL; it expects the policy to
        // be hosted in one of the repositories listed in the project.
        String policyUrl = findPolicyUrlFromRepositories();
        
        if (policyUrl == null) {
            throw new MojoExecutionException("Policy file could not be located in configured remote repositories. Ensure repository contains '" + versionPolicyGroup + "/" + versionPolicyArtifactId + "'.");
        }

        DependencyVersionPolicy policy = loadVersionPolicy(policyUrl);

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

    /**
     * Attempt to locate the policy XML file in the project's configured
     * remote repositories. Returns the first reachable URL or null if none
     * found.
     */
    private String findPolicyUrlFromRepositories() {
        List<ArtifactRepository> repos = project.getRemoteArtifactRepositories();
        if (repos == null || repos.isEmpty()) return null;

        for (ArtifactRepository repo : repos) {
            try {
                String base = repo.getUrl();
                if (!base.endsWith("/")) base += "/";
                // If an artifactId is provided, attempt to read maven-metadata.xml
                // and resolve the latest available version for the artifact. This
                // follows the standard Maven repository layout:
                // {base}/{groupPath}/{artifactId}/{version}/{artifactId}-{version}.xml
                if (versionPolicyArtifactId != null && !versionPolicyArtifactId.isBlank()) {
                    String groupPath = versionPolicyGroup.replace('.', '/');
                    String metadataUrl = base + groupPath + "/" + versionPolicyArtifactId + "/maven-metadata.xml";
                    try (InputStream metaIs = new URL(metadataUrl).openStream()) {
                        // parse metadata and pick best version
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        Document doc = db.parse(metaIs);

                        String selectedVersion = null;
                        // prefer <release>
                        NodeList releaseNodes = doc.getElementsByTagName("release");
                        if (releaseNodes != null && releaseNodes.getLength() > 0) {
                            selectedVersion = releaseNodes.item(0).getTextContent();
                        }

                        // then prefer <latest>
                        if (selectedVersion == null) {
                            NodeList latestNodes = doc.getElementsByTagName("latest");
                            if (latestNodes != null && latestNodes.getLength() > 0) {
                                selectedVersion = latestNodes.item(0).getTextContent();
                            }
                        }

                        // otherwise pick the highest from <versions>
                        if (selectedVersion == null) {
                            NodeList versions = doc.getElementsByTagName("version");
                            DefaultArtifactVersion best = null;
                            for (int i = 0; i < versions.getLength(); i++) {
                                String v = versions.item(i).getTextContent();
                                if (v == null || v.isBlank()) continue;
                                DefaultArtifactVersion dav = new DefaultArtifactVersion(v.trim());
                                if (best == null || dav.compareTo(best) > 0) {
                                    best = dav;
                                }
                            }
                            if (best != null) selectedVersion = best.toString();
                        }

                        if (selectedVersion != null && !selectedVersion.isBlank()) {
                            String candidate = base + groupPath + "/" + versionPolicyArtifactId + "/" + selectedVersion + "/" + versionPolicyArtifactId + "-" + selectedVersion + ".xml";
                            try (InputStream is = new URL(candidate).openStream()) {
                                return candidate;
                            }
                        }
                        // if metadata exists but produced no usable version, fall through to legacy lookup
                    } catch (Exception e) {
                        getLog().debug("Failed to read or parse maven-metadata.xml at " + metadataUrl + ": " + e.getMessage());
                        // continue to fallback behavior
                    }
                }
            } catch (Exception e) {
                // try next repository
                getLog().debug("Policy not found at repository or repository unreachable: " + e.getMessage());
            }
        }
        return null;
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
