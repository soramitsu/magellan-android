# magellan-android
This repository is managed by Terraform!

## Soramitsu maps library 
### How to add maps to your project 
This library destibutes as a private library. We are using private Nexus so you have to do some preparations to download this library and use it in your project  
1. Create or update your **private(!)** `~/.gradle/gradle.properties`   
2. In that new file add two variables: `SORAMITSU_MAVEN_USER_NAME` and `SORAMITSU_MAVEN_USER_PASSWORD` with corresponding data 

Example of `~/.gradle/gradle.properties`:
```
SORAMITSU_MAVEN_USER_NAME = dselivanov
SORAMITSU_MAVEN_USER_PASSWORD = *******
```
You can check your credentials by sign in in [Nexus](https://nexus.iroha.tech). If you don't have an access to the Nexus, contact DevOps team or your team lead  
3. Add following repository to your `build.gradle` file 
```groovy
allprojects {
    repositories {
        maven {
            url "https://nexus.iroha.tech/repository/maven-soramitsu-private/"
            credentials {
                username = System.getenv("NEXUS_USR") ?: SORAMITSU_MAVEN_USER_NAME
                password = System.getenv("NEXUS_PSW") ?: SORAMITSU_MAVEN_USER_PASSWORD
            }
        }
    }
}
```
*Note:* `NEXUS_USR` *and* `NEXUS_PSW` *may depend of your* `Jenkinsfile` *and CI configuration* 
4. Add `implementation` to your module's `build.gradle` file 
```groovy 
dependencies {
    implementation 'jp.co.soramitsu:map:0.1.0'
}
```
