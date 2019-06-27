#!groovy
build('columbus', 'java-maven') {
    checkoutRepo()
    loadBuildUtils()

    def buildContainer = docker.image('dr2.rbkmoney.com/rbkmoney/postgres-geodata:0eb52256576ec22f89fadc3e7fe505b692e838a3')
    runStage('Pull build image') {
        docker.withRegistry('dr2.rbkmoney.com', 'jenkins_harbor') {
            buildContainer.pull()
        }
        buildContainer = docker.image(registry + '/rbkmoney/postgres-geodata:0eb52256576ec22f89fadc3e7fe505b692e838a3')
    }

    def javaServicePipeline
    runStage('load JavaService pipeline') {
        javaServicePipeline = load("build_utils/jenkins_lib/pipeJavaService.groovy")
    }

    def serviceName = env.REPO_NAME
    def mvnArgs = '-DjvmArgs="-Xmx256m"'
    def useJava11 = true

    javaServicePipeline(serviceName, useJava11, mvnArgs)
}