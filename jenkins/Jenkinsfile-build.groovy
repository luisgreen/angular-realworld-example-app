/**
* SUBSCRIPTION MANAGER Jenkinsfile
* MAINTAINER: Luis Chacón <luisgreen at gmail dot com>
*/
def APP_VERSION
def IMAGE_TAG
def TEST_BASE_URL
def GCP_WORKLOAD_NAME

pipeline {
  agent { label 'code-worker' }

  options {
    timestamps()
  }

  environment {
    GCP_PROJECT_NAME  = "${env.GCP_PROJECT_NAME}"
    APP_NAME      = "frontend"
  }
  stages {
    stage('Checkout Source Control') {
      steps {
        script {
            IMAGE_TAG="gcr.io/${GCP_PROJECT_NAME}/${APP_NAME}:latest"
          }
        }
    }
    stage('Building App Image') {
      steps {
        sh "docker build -t ${IMAGE_TAG} ."
      }
    }
    stage('Pushing App Image to Registry') {
      steps {
        sh "docker push ${IMAGE_TAG}"
      }
    }
  }
  post {
    success {
      script {
        echo "Success"
      }
    }
    cleanup {
      script {
        sh "docker system prune -a --force"
      }
    }
  }
}
