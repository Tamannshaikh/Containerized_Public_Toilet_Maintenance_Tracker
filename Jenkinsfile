pipeline {
    agent any

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['staging', 'production'], description: 'Deployment environment target (default: staging)')
        string(name: 'DEPLOY_PORT', defaultValue: '8082', description: 'Application port for staging deployment (default: 8082, isolates from existing 8081 process)')
        booleanParam(name: 'AUTO_DEPLOY', defaultValue: true, description: 'Automatically deploy to staging and execute smoke tests')
    }

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

        stage('Deploy Application') {
            when {
                expression {
                    return params.AUTO_DEPLOY == true
                }
            }
            steps {
                echo '====================================================='
                echo "STAGE 7: Deploying ${APP_NAME} to ${params.DEPLOY_ENV}"
                echo '====================================================='
                script {
                    if (params.DEPLOY_ENV == 'production') {
                        echo 'SECURITY NOTICE: Automated deployment to production is restricted. Only staging deployment is enabled in this pipeline.'
                        return
                    }

                    // Dynamically preserve port 8081 if occupied and route staging to port 8082
                    def targetPort = params.DEPLOY_PORT
                    def portCheck = bat(returnStatus: true, script: "@echo off & netstat -ano | findstr \":${targetPort}\" > nul")
                    if (portCheck == 0 && targetPort == '8081') {
                        echo '[DEPLOY] Notice: Port 8081 is currently occupied by existing application process.'
                        echo '[DEPLOY] Preserving active service on 8081; re-routing staging deployment to port 8082.'
                        targetPort = '8082'
                    }

                    env.ACTIVE_DEPLOY_PORT = targetPort
                    echo "Target Environment : ${params.DEPLOY_ENV}"
                    echo "Deployment Port    : ${env.ACTIVE_DEPLOY_PORT}"
                    echo "Deployment Artifact: target/${JAR_NAME}"

                    // Step 7.1: Prepare staging deployment artifact directory
                    bat '''
                        @echo off
                        echo [DEPLOY] Preparing deployment directory: deploy\\staging
                        if not exist deploy\\staging mkdir deploy\\staging
                        copy /Y target\\public-toilet-maintenance-tracker-1.0.0-SNAPSHOT.jar deploy\\staging\\app.jar > nul
                        echo [DEPLOY] Verified artifact staged at deploy\\staging\\app.jar
                    '''

                    // Step 7.2: Verify Docker daemon availability safely
                    def dockerRunning = false
                    try {
                        def dockerCheck = bat(returnStatus: true, script: '@echo off & docker info > nul 2>&1')
                        dockerRunning = (dockerCheck == 0)
                    } catch (Exception e) {
                        dockerRunning = false
                    }

                    if (dockerRunning) {
                        echo '[DEPLOY] Docker daemon detected. Building container image public-toilet-maintenance-tracker:1.0.0...'
                        bat 'docker build -t public-toilet-maintenance-tracker:1.0.0 .'
                        echo '[DEPLOY] Docker image built and tagged successfully.'
                    } else {
                        echo '[DEPLOY] Note: Docker daemon is currently stopped / unavailable in this Windows environment.'
                        echo '[DEPLOY] Executing standalone local staging deployment.'
                    }

                    // Step 7.3: Clean previous staging instance on targetPort (if not 8081/8080) and launch newly built JAR
                    bat """
                        @echo off
                        echo [DEPLOY] Checking and preparing port ${env.ACTIVE_DEPLOY_PORT}...
                        powershell -Command "if (${env.ACTIVE_DEPLOY_PORT} -ne 8081 -and ${env.ACTIVE_DEPLOY_PORT} -ne 8080) { Get-NetTCPConnection -LocalPort ${env.ACTIVE_DEPLOY_PORT} -ErrorAction SilentlyContinue | ForEach-Object { Write-Output ('Stopping previous staging process: PID ' + \$_.OwningProcess); Stop-Process -Id \$_.OwningProcess -Force -ErrorAction SilentlyContinue } }"
                        echo [DEPLOY] Launching newly built JAR: deploy\\staging\\app.jar on port ${env.ACTIVE_DEPLOY_PORT}...
                        start /B java -jar deploy\\staging\\app.jar --server.port=${env.ACTIVE_DEPLOY_PORT} > deploy\\staging\\app.log 2>&1
                    """

                    // Step 7.4: Actively wait for application startup and verify health (fails stage if unhealthy)
                    echo "[DEPLOY] Waiting for application startup on port ${env.ACTIVE_DEPLOY_PORT}..."
                    bat """
                        @echo off
                        powershell -Command "\$maxAttempts = 15; \$healthy = \$false; for (\$i = 1; \$i -le \$maxAttempts; \$i++) { Start-Sleep -Seconds 2; try { \$r = Invoke-WebRequest -Uri 'http://localhost:${env.ACTIVE_DEPLOY_PORT}/api/v1/health' -UseBasicParsing -TimeoutSec 3; if (\$r.StatusCode -eq 200) { \$data = \$r.Content | ConvertFrom-Json; if (\$data.status -eq 'UP') { Write-Output ('[DEPLOY] Application is UP and HEALTHY on port ${env.ACTIVE_DEPLOY_PORT} (Attempt ' + \$i + ')'); \$healthy = \$true; break } } } catch { Write-Output ('[DEPLOY] Waiting for application initialization... (Attempt ' + \$i + '/' + \$maxAttempts + ')') } }; if (-not \$healthy) { Write-Error '[DEPLOY] ERROR: Newly deployed application failed to become healthy within timeout!'; exit 1 }"
                    """
                }
            }
        }

        stage('Smoke Test and Health Verification') {
            when {
                expression {
                    return params.AUTO_DEPLOY == true && params.DEPLOY_ENV != 'production'
                }
            }
            steps {
                echo '====================================================='
                echo "STAGE 8: Executing Smoke Test & Health Verification on Port ${env.ACTIVE_DEPLOY_PORT ?: params.DEPLOY_PORT}"
                echo '====================================================='
                script {
                    def probePort = env.ACTIVE_DEPLOY_PORT ?: params.DEPLOY_PORT
                    echo "Probing Newly Deployed Health Endpoint: http://localhost:${probePort}/api/v1/health"
                    bat """
                        @echo off
                        powershell -Command "\$resp = Invoke-WebRequest -Uri 'http://localhost:${probePort}/api/v1/health' -UseBasicParsing -TimeoutSec 10; Write-Output ('HTTP Status Code: ' + \$resp.StatusCode); \$json = \$resp.Content | ConvertFrom-Json; Write-Output ('Health JSON Status: ' + \$json.status); Write-Output ('Service Name: ' + \$json.service); Write-Output ('Service Version: ' + \$json.version); if (\$resp.StatusCode -eq 200 -and \$json.status -eq 'UP') { Write-Output '>>> SMOKE TEST PASSED: Newly deployed application verified healthy on port ${probePort}! <<<'; exit 0 } else { Write-Error '>>> SMOKE TEST FAILED: Health verification did not return HTTP 200 and status UP! <<<'; exit 1 }"
                    """

                    echo "Probing Newly Deployed Facilities Endpoint: http://localhost:${probePort}/api/v1/facilities"
                    bat """
                        @echo off
                        powershell -Command "\$fac = Invoke-RestMethod -Uri 'http://localhost:${probePort}/api/v1/facilities' -Method Get -TimeoutSec 10; Write-Output ('SUCCESS: Facilities endpoint returned ' + \$fac.Count + ' active facilities on port ${probePort}.'); exit 0"
                    """
                }
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
            echo "SUCCESS: Jenkins CI/CD Pipeline for ${APP_NAME} completed successfully!"
            echo "Target Environment : ${params.DEPLOY_ENV ?: 'staging'}"
            echo "Deployment Port     : ${env.ACTIVE_DEPLOY_PORT ?: params.DEPLOY_PORT ?: '8082'}"
            echo "Application URL     : http://localhost:${env.ACTIVE_DEPLOY_PORT ?: params.DEPLOY_PORT ?: '8082'}"
            echo "Health Endpoint     : http://localhost:${env.ACTIVE_DEPLOY_PORT ?: params.DEPLOY_PORT ?: '8082'}/api/v1/health"
            echo "Facilities API      : http://localhost:${env.ACTIVE_DEPLOY_PORT ?: params.DEPLOY_PORT ?: '8082'}/api/v1/facilities"
            echo "Preserved Service   : http://localhost:8081 (Active instance untouched)"
            echo "Archived Artifact   : target/${JAR_NAME}"
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
