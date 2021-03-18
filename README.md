# magellan-android
This repository is managed by Terraform!

## Soramitsu maps library 
### How to add maps to your project 
1. Add following repository to your `build.gradle` file
```groovy
allprojects {
    repositories {
        maven {
            url "https://nexus.iroha.tech/repository/maven-soramitsu/"
        }
    }
}
```
2. Add `implementation` to your module's `build.gradle` file
```groovy 
dependencies {
    implementation 'jp.co.soramitsu:map:1.1.10'
}
```
