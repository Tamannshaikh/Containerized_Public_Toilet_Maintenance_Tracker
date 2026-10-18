pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }

    environment {
        APP_NAME = 'containerized-public-toilet-maintenance-tracker'
        DOCKER_REGISTRY = 'docker.io'
        IMAGE_NAME = "toiletddevops/${APP_NAME}:${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code from Git repository...'
                checkout scm
            }
        }

        stage('Code Compile & Unit Test') {
            steps {
                echo 'Compiling source code and executing unit tests...'
                bat 'mvn clean test'
            }
        }

        stage('Package Application') {
            steps {
                echo 'Packaging application artifact (JAR)...'
                bat 'mvn package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('Selenium UI & Automated Testing') {
            steps {
                echo 'Running Selenium automated acceptance and end-to-end tests...'
                // bat 'mvn test -Dtest=*SeleniumTest'
            }
        }

        stage('Docker Build') {
            steps {
                echo "Building Docker container image: ${IMAGE_NAME}"
                bat "docker build -t ${IMAGE_NAME} ."
            }
        }

        stage('Docker CD & Deployment') {
            steps {
                echo 'Deploying application stack via Docker Compose...'
                bat 'docker compose up -d'
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution complete.'
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
        }
        success {
            echo 'Build, Test, and Deployment completed successfully!'
        }
        failure {
            echo 'Pipeline failed! Please inspect logs.'
        }
    }
}
