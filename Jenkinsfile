pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk   'JDK17'
    }

    environment {
        DOCKER_USER = 'admin'          // your Docker Hub username
        IMAGE       = "${DOCKER_USER}/java-k8s-app"
        TAG         = "v${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build & Test') {
            steps { sh 'mvn clean test' }
            post {
                always { junit 'target/surefire-reports/*.xml' }
            }
        }

        stage('Docker Build') {
            steps { sh 'docker build -t $IMAGE:$TAG -t $IMAGE:latest .' }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                        usernameVariable: 'U', passwordVariable: 'P')]) {
                    sh '''
                        echo "$P" | docker login -u "$U" --password-stdin
                        docker push $IMAGE:$TAG
                        docker push $IMAGE:latest
                    '''
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig-cred', variable: 'KUBECONFIG')]) {
                    sh '''
                        sed "s|admin/java-k8s-app:v1|$IMAGE:$TAG|" k8s/deployment.yaml | kubectl apply -f -
                        kubectl apply -f k8s/service.yaml
                        kubectl rollout status deployment/java-app --timeout=120s
                        kubectl get pods,svc
                    '''
                }
            }
        }
    }

    post {
        success { echo 'Pipeline succeeded' }
        failure { echo 'Pipeline failed' }
    }
}
