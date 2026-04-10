pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "venkat8430/product-service"
        DOCKER_TAG   = "${BUILD_NUMBER}"
        MANIFEST_REPO = "git@github.com:vnukala84/product-service-manifests.git"
        MANIFEST_BRANCH = "arg-helm-doc-jen"
        HELM_PATH = "helm/product-service"
        GIT_CREDENTIALS_ID = "git-ssh-credentials"
        DOCKER_CREDENTIALS_ID = "dockerhub-credentials"
    }

    stages {

        stage('Checkout Application Code') {
            steps {
                git branch: 'main',
                    url: 'git@github.com:vnukala84/product-service.git'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: "${DOCKER_CREDENTIALS_ID}",
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {

                        sh """
                        echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                        docker push ${DOCKER_IMAGE}:${DOCKER_TAG}
                        docker logout
                        """
                    }
                }
            }
        }

        stage('Update Helm Chart in Manifests Repo') {
            steps {
                script {
                    sh """
                    rm -rf manifests-repo

                    git clone -b ${MANIFEST_BRANCH} ${MANIFEST_REPO} manifests-repo

                    cd manifests-repo/${HELM_PATH}

                    # Update image tag in values.yaml
                    sed -i 's/tag:.*/tag: "${DOCKER_TAG}"/g' values.yaml

                    # Optional: update image repository if needed
                    # sed -i 's|repository:.*|repository: ${DOCKER_IMAGE}|g' values.yaml

                    cd ../../

                    git config user.email "jenkins@demo.com"
                    git config user.name "Jenkins"

                    git add .
                    git commit -m "Update image tag to ${DOCKER_TAG}" || echo "No changes to commit"
                    git push origin ${MANIFEST_BRANCH}
                    """
                }
            }
        }
    }

    post {
        success {
            echo "✅ Build successful. Argo CD will sync automatically."
        }
        failure {
            echo "❌ Build failed."
        }
    }
}

