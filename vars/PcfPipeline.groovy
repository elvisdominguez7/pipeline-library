def call(body){

def pipelineParams= [:]
body.resolveStrategy = Closure.DELEGATE_FIRST
body.delegate = pipelineParams
body()

    pipeline {
        agent any
        
        environment { 
	    GIT_BOT = "${env.git_bot}"
	        
    	}
        
        stages {
            stage('checkout git') {
                steps {
                    git branch: pipelineParams.branch, credentialsId: "${GIT_BOT}", url: pipelineParams.scmUrl
                }
            }

            stage('Build') {
                steps {
                    sh 'mvn clean package -DskipTests=true'
                }
            }

            stage ('Test') {
                steps {
                    parallel (
                        "unit tests": { sh 'mvn test' },
                        "integration tests": { sh 'mvn integration-test' }
                    )
                }
            }

            stage('Deploy To Dev'){
                steps {
                    deploy(pipelineParams.developmentServer, pipelineParams.serverPort)
                }
            }

            stage('Deploy To System'){
                steps {
                    deploy(pipelineParams.stagingServer, pipelineParams.serverPort)
                }
            }

            stage('Deploy To Performance'){
                steps {
                    deploy(pipelineParams.productionServer, pipelineParams.serverPort)
                }
            }
            
            stage('Deploy To Pre-Prod'){
	        steps {
	               deploy(pipelineParams.productionServer, pipelineParams.serverPort)
	        }
            }
            
            stage('Deploy To Production'){
	    	  steps {
	    	       deploy(pipelineParams.productionServer, pipelineParams.serverPort)
	    	  }
            }
        }
        
        post {
          success {
	        mail to: pipelineParams.email, subject: 'Pipeline Successfully Executed ', body: "${env.BUILD_URL}"
          }
            
          failure {
                mail to: pipelineParams.email, subject: 'Pipeline failed', body: "${env.BUILD_URL}"
          }
        }
    }
}