#!groovy
build('columbus', 'docker-host') {
    checkoutRepo()

    def serviceName = "columbus"
    def baseImageTag = "70f9fa4ba9bb06cc36b292862ab0555f3bad6321"
    def buildImageTag = "7372dc01bf066b5b26be13d6de0c7bed70648a26"
    def dbHostName = "clbdb"
    def dbImage = "dr.rbkmoney.com/rbkmoney/postgres-geodata:7535abe1dcd9942f9289afd5f665dc9e39c56f90"
    def mvnArgs = '-DjvmArgs="-Xmx256m"'

    // service name - usually equals artifactId
    env.SERVICE_NAME = serviceName
    // service java image tag
    env.BASE_IMAGE_TAG = baseImageTag
    // build container image tag
    env.BUILD_IMAGE_TAG = buildImageTag
    // data base name
    env.DB_NAME = serviceName
    // host url for database. If null - DB will not start
    env.DB_HOST_NAME = dbHostName
    // mvnArgs - arguments for mvn install in build container. For exmple: ' -DjvmArgs="-Xmx256m" '

    // Using withRegistry() for auth on docker hub server.
    // Pull it to local images with short name and reopen it with full name, to exclude double naming problem
    def buildContainer = docker.image('rbkmoney/build:$BUILD_IMAGE_TAG')
    runStage('Pull build image') {
        docker.withRegistry('https://dr.rbkmoney.com/v2/', 'dockerhub-rbkmoneycibot') {
            buildContainer.pull()
        }
        buildContainer = docker.image('dr.rbkmoney.com/rbkmoney/build:$BUILD_IMAGE_TAG')
    }

    def postgresImage;
    try {
        // Start db if necessary.

        def insideParams = '';
        if (dbHostName != null) {
            runStage('Run PostgresDB container') {
                postgresImage = docker.image("$dbImage").run(
                        '-e POSTGRES_PASSWORD=postgres ' +
                                '-e POSTGRES_USER=postgres ' +
                                '-e POSTGRES_DB=$DB_NAME ' +
                                '--name=dependOn'
                )
                insideParams = ' --link=dependOn:$DB_HOST_NAME --volumes-from=dependOn'
            }
        }
        // Run mvn and generate docker file
        runStage('Execute build container') {
            withCredentials([[$class: 'FileBinding', credentialsId: 'java-maven-settings.xml', variable: 'SETTINGS_XML']]) {
                buildContainer.inside(insideParams) {
                    def mvn_command_arguments = ' --batch-mode --settings  $SETTINGS_XML -P ci ' +
                            '-Ddockerfile.base.service.tag=$BASE_IMAGE_TAG ' +
                            '-Ddockerfile.build.container.tag=$BUILD_IMAGE_TAG ' +
                            '-Ddb.url.host.name=$DB_HOST_NAME ' +
                            " ${mvnArgs}"
                    if (env.BRANCH_NAME == 'master') {
                        sh 'mvn deploy' + mvn_command_arguments
                    } else {
                        sh 'mvn package' + mvn_command_arguments
                    }
                }
            }
        }
    }
    finally {
        if (postgresImage != null) {
            runStage('Stop PostgresDB container') {
                postgresImage.stop()
            }
        }
    }

    def serviceImage;
    def imgShortName = 'rbkmoney/' + env.SERVICE_NAME + ':' + '$COMMIT_ID';
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