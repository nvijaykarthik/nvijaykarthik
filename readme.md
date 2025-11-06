mvn deploy:deploy-file -DgroupId=com.example.metadata \
    -DartifactId=dependency-version-policy -Dversion=1.0 -Dpackaging=xml \
    -Dfile=dependencyVersionPolicy.xml -DrepositoryId=your-repo-id \
    -Durl=https://your.maven.repo/repository/releases -DgeneratePom=false