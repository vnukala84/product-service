pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    environment {
        DOCKER_IMAGE = "venkat8430/product-service"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        GIT_REPO = "https://github.com/<your-username>/product-service-manifests.git"
    }

    stages {

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE:$IMAGE_TAG .'
            }
        }

        stage('Docker Login') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-creds', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                    sh 'echo $PASS | docker login -u $USER --password-stdin'
                }
            }
        }

        stage('Docker Push') {
            steps {
                sh 'docker push $DOCKER_IMAGE:$IMAGE_TAG'
            }
        }

        stage('Update Manifest Repo') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'git-creds', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                    sh '''
                    rm -rf manifests-repo
                    git clone -b master https://github.com/vnukala84/product-service-manifests.git 

                    cd manifests-repo/k8s

                    # Update image tag
                    sed -i "s|image: .*|image: venkat8430/product-service:${BUILD_NUMBER}|" deployment.yaml

                    git config user.name "jenkins"
                    git config user.email "jenkins@example.com"

                    git add .
                    git commit -m "Update image to ${BUILD_NUMBER}"
                    git push
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "✅ Image updated and pushed to Git. ArgoCD will deploy automatically."
        }
        failure {
            echo "❌ Pipeline failed"
        }
    }
}
