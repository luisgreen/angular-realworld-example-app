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
    K8S_WORKLOAD    = "frontend"
  }
  stages {
    stage('Patching Workload') {
      steps {
         script {
          withCredentials([
            file(credentialsId: 'notejam_gke_cluster', variable: 'KUBECONFIG')
          ]) {
            echo "Deploying: ${IMAGE_TAG}"
            sh "kubectl patch deployment ${K8S_WORKLOAD} -p \"{\\\"spec\\\":{\\\"template\\\":{\\\"metadata\\\":{\\\"annotations\\\":{\\\"date\\\":\\\"`date +'%s'`\\\"}}}}}\""
          }
        }
      }
    }
    stage('K8s API Deploy Check') {
      steps {
        withCredentials([
            file(credentialsId: 'notejam_gke_cluster', variable: 'KUBECONFIG')
          ]) {
          timeout(time: 5, unit: 'MINUTES') {
            retry(3) {
              echo "Checking ${K8S_WORKLOAD} Deploy Health Check"
              sh "kubectl rollout status deploy/${K8S_WORKLOAD} | grep successfully"
            }
          }
        }
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
        echo "Cleanup"
      }
    }
  }
}
