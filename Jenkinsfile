def dockerImage = 'build-tools/android-build-box:latest'
def branchesList = ['master']
pipeline {
    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timestamps()
        disableConcurrentBuilds()
    }
    agent {
        docker {
            label 'd3-build-agent'
            image dockerImage
            registryUrl 'https://docker.soramitsu.co.jp'
            registryCredentialsId 'bot-build-tools-ro'
            // Uncomment if you need docker sock inside
            // args '-v /var/run/docker.sock:/var/run/docker.sock -v /tmp:/tmp'
        }
    }
    stages {
        stage('Build') {
            steps {
                script {
                    sh "./gradlew clean Build"
                }
            }
        }
        stage('Test') {
            steps {
                script {
                    sh "./gradlew :library:test"
                }
            }
        }
  // todo: add sonar configuration
  //      stage('Sonar') {
  //          steps {
  //              withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
  //                  sh "./gradlew sonarqube -x test -Dsonar.host.url=https://sonar.soramitsu.co.jp -Dsonar.login=${SONAR_TOKEN}"
  //              }
  //          }
  //      }
        stage('Push artifacts') {
            when {
                expression { return (env.GIT_BRANCH in branchesList ) }
            }
            environment {
                NEXUS_URL = "https://nexus.iroha.tech/repository/maven-soramitsu/"
                NEXUS = credentials('bot-soramitsu-rw') // -> NEXUS_USR NEXUS_PSW
            }
            steps {
                script {
                    sh "./gradlew :library:publishReleasePublicationToMavenRepository"
                }
            }
        }
    }
    post {
        cleanup {
            cleanWs()
        }
    }
}
