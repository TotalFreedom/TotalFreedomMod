pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                mvn -B package --file pom.xml
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true 
            }
        }
    }
}
