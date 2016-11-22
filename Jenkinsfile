#!groovy
build('columbus', 'docker-host') {
    checkoutRepo()

    def serviceName = "columbus"
    def baseImageTag = "70f9fa4ba9bb06cc36b292862ab0555f3bad6321"
    def mvnArgs = '-DjvmArgs="-Xmx256m"'

    // Run mvn and generate docker file
    runStage('Maven package') {
        withCredentials([[$class: 'FileBinding', credentialsId: 'java-maven-settings.xml', variable: 'SETTINGS_XML']]) {
            def mvn_command_arguments = ' --batch-mode --settings  $SETTINGS_XML -P ci' +
                    "-Ddockerfile.base.service.tag=${baseImageTag} " +
                    "-Dgit.branch=${env.BRANCH_NAME} " +
                    '-Dmvn.java.version=$MVN_VERSION' +
                    " ${mvnArgs}"
            if (env.BRANCH_NAME == 'master') {
                sh 'mvn deploy' + mvn_command_arguments
            } else {
                sh 'mvn package' + mvn_command_arguments
            }
        }
    }

    def serviceImage;
    def imgShortName = 'rbkmoney/' + "${serviceName}" + ':' + '$COMMIT_ID';
    getCommitId()
    runStage('Build Service image') {
        serviceImage = docker.build(imgShortName, '-f ./target/Dockerfile ./target')
    }

    try {
        if (env.BRANCH_NAME == 'master') {
            runStage('Push Service image') {
                docker.withRegistry('https://dr.rbkmoney.com/v2/', 'dockerhub-rbkmoneycibot') {
                    serviceImage.push();
                }
                // Push under 'withRegistry' generates 2d record with 'long name' in local docker registry.
                // Untag the long-name
                sh "docker rmi dr.rbkmoney.com/${imgShortName}"
            }
        }
    }
    finally {
        runStage('Remove local image') {
            // Remove the image to keep Jenkins runner clean.
            sh "docker rmi ${imgShortName}"
        }
    }
}