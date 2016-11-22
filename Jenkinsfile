#!groovy
build('columbus', 'docker-host') {
    checkoutRepo()

    //save mvn/java version
    sh 'mvn -v > .mvn_version'
    env.MVN_VERSION = readFile('.mvn_version').trim()
    sh 'rm .mvn_version'

    // Run mvn and generate docker file
    runStage('Maven package') {
        withCredentials([[$class: 'FileBinding', credentialsId: 'java-maven-settings.xml', variable: 'SETTINGS_XML']]) {
            def mvn_command_arguments = ' --batch-mode --settings  $SETTINGS_XML -P ci' +
                    "-Ddockerfile.base.service.tag=${baseImageTag} " +
                    "-Dgit.branch=${env.BRANCH_NAME} " +
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