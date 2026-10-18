pipeline {
    agent any

    tools {
        // Matches configured installations in Jenkins Global Tool Configuration
        maven 'Maven'
        jdk 'JDK-21'
    }

    environment {
        APP_NAME = 'public-toilet-maintenance-tracker'
        APP_VERSION = '1.0.0-SNAPSHOT'
        JAR_NAME = "${APP_NAME}-${APP_VERSION}.jar"
    }

    stages {
        stage('Checkout') {
            steps {
                echo '====================================================='
                echo 'STAGE 1: Checking out source code from Git repository'
                echo '====================================================='
                checkout scm
            }
        }

        stage('Verify Environment') {
            steps {
                echo '====================================================='
                echo 'STAGE 2: Verifying Java and Maven runtime environment'
                echo '====================================================='
                bat 'java -version'
                bat 'mvn -version'
            }
        }

        stage('Compile') {
            steps {
                echo '====================================================='
                echo 'STAGE 3: Compiling project source code'
                echo '====================================================='
                bat 'mvn clean compile'
            }
        }

        stage('Run Automated Tests') {
            steps {
                echo '====================================================='
                echo 'STAGE 4: Executing automated unit & integration tests'
                echo '====================================================='
                bat 'mvn test'
            }
        }

        stage('Package Application') {
            steps {
                echo '====================================================='
                echo 'STAGE 5: Packaging executable Spring Boot JAR'
                echo '====================================================='
                bat 'mvn package -DskipTests'
            }
        }

        stage('Archive Artifacts') {
            steps {
                echo '====================================================='
                echo 'STAGE 6: Archiving build artifacts for distribution'
                echo '====================================================='
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: false
            }
        }
    }

    post {
        always {
            echo 'Recording automated test results in JUnit format...'
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
        }
        success {
            echo '====================================================='
            echo "SUCCESS: Jenkins CI Pipeline for ${APP_NAME} completed successfully!"
            echo "Archived Artifact: target/${JAR_NAME}"
            echo '====================================================='
        }
        failure {
            echo '====================================================='
            echo "FAILURE: Jenkins CI Pipeline for ${APP_NAME} failed!"
            echo 'Inspect the console output and surefire reports above.'
            echo '====================================================='
        }
    }
}
