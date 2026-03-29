pipeline {
    agent any

    environment {
        IMAGE_NAME = "venkat8430/product-service"
        MANIFEST_REPO = "git@github.com:vnukala84/product-service-manifests.git"
    }

    tools {
        maven 'Maven'
    }

    stages {

        stage('Checkout Source Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/vnukala84/product-service.git'
            }
        }

        stage('Build JAR') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    dockerImage = docker.build("${IMAGE_NAME}:${BUILD_NUMBER}")
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'docker-hub-creds') {
                        dockerImage.push("${BUILD_NUMBER}")
                    }
                }
            }
        }

        stage('Update Manifest Repo') {
            steps {
                sshagent(['github-ssh-key']) {
                    sh '''
                    rm -rf manifests-repo
                    git clone ${MANIFEST_REPO} manifests-repo

                    cd manifests-repo

                    # Update image tag
                    sed -i "s|image: .*|image: ${IMAGE_NAME}:${BUILD_NUMBER}|g" deployment.yaml

                    git add .
                    git commit -m "Update image to ${BUILD_NUMBER}" || echo "No changes to commit"
                    git push origin main
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline completed successfully"
        }
        failure {
            echo "❌ Pipeline failed"
        }
    }
}
