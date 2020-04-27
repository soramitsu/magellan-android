def dockerImage = 'build-tools/android-build-box:latest'

node('d3-build-agent') {
    properties(
	    [
		    disableConcurrentBuilds()
		]
    )
    timestamps {
        try {
            stage('Git pull'){
                def scmVars = checkout scm
                env.GIT_BRANCH = scmVars.GIT_LOCAL_BRANCH
                env.GIT_COMMIT = scmVars.GIT_COMMIT
            }
            withCredentials([
                [$class: 'UsernamePasswordMultiBinding', credentialsId: 'nexus-capital-rw', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASSWORD']
                ])
            {
                docker.withRegistry('https://nexus.iroha.tech:19003', 'jenkins_nexus_creds') {
                    docker.image("${dockerImage}").inside() {
                        stage('Build library') {
                            sh "./gradlew clean Build"
                        }
                        stage('Test') {
                            sh "./gradlew :library:test"
                        }
                        if (env.GIT_BRANCH == 'master' || env.TAG_NAME) {
                            stage('Deploy library') {
                                sh "./gradlew :library:publishReleasePublicationToMavenRepository"
                            }
                        }
                    }
                }
            }
        } catch (e) {
            print(e)
            currentBuild.result = 'FAILURE'
        } finally {
            cleanWs()
        }
    }
}
