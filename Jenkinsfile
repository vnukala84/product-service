pipeline {
agent any

```
environment {
    IMAGE_NAME    = "venkat8430/product-service"
    IMAGE_TAG     = "${BUILD_NUMBER}"
    MANIFEST_REPO = "git@github.com:vnukala84/product-service-manifests.git"
}

tools {
    maven 'Maven3'
    jdk 'JDK17'
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
            sh '''
            echo "Java version:"
            java -version

            echo "Maven version:"
            mvn -version

            mvn clean package -DskipTests
            '''
        }
    }

    stage('Build Docker Image') {
        steps {
            sh '''
            docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
            '''
        }
    }

    stage('Push Docker Image') {
        steps {
            withCredentials([usernamePassword(
                credentialsId: 'docker-hub-creds',
                usernameVariable: 'DOCKER_USER',
                passwordVariable: 'DOCKER_PASS'
            )]) {
                sh '''
                echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                docker push ${IMAGE_NAME}:${IMAGE_TAG}
                docker logout
                '''
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

                git checkout main || git checkout -b main

                sed -i "s|image: .*|image: ${IMAGE_NAME}:${IMAGE_TAG}|g" deployment.yaml

                git config user.name "Jenkins"
                git config user.email "jenkins@local"

                git add .
                git commit -m "Update image to ${IMAGE_TAG}" || echo "No changes to commit"
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
```

}

