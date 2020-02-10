pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                mvn build 
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true 
            }
        }
    }
}
