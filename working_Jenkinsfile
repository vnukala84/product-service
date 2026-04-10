pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    environment {
        DOCKER_IMAGE = "venkat8430/product-service"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
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

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                    sh '''
                    export KUBECONFIG=$KUBECONFIG

                    # Update image in deployment (NO downtime rollout)
                    kubectl set image deployment/product-service \
                    product-service=$DOCKER_IMAGE:$IMAGE_TAG

                    # Wait for rollout
                    kubectl rollout status deployment/product-service
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "✅ Deployment successful! Image: $DOCKER_IMAGE:$IMAGE_TAG"
        }
        failure {
            echo "❌ Pipeline failed"
        }
    }
}
