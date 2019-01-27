import org.edominguez.utils.PcfUtils;

def call(body){

def pipelineParams= [:]
body.resolveStrategy = Closure.DELEGATE_FIRST
body.delegate = pipelineParams
body()

    pipeline {
        agent any
        
	tools{
		maven 'apache-maven-3.5.4'
	}
        environment { 
	    GIT_BOT = "${env.git_bot}"
	        
    	}
	    
        stages {
            stage('Pipeline Setup') {
                steps {
			script{ 
				PcfUtils utils = new PcfUtils(pipelineParams.domain,pipelineParams.org)
				utils.login(pipelineParams.userName, pipelineParams.password, pipelineParams.enviromentName)
			}
                }
        }
        stages {
            stage('Checkout Project') {
                steps {
                    git branch: pipelineParams.branch, credentialsId: "${GIT_BOT}", url: pipelineParams.scmUrl
                }
        }

            stage('Build') {
                steps {
                    bat "mvn clean package -DskipTests=true"
                }
            }

            stage ('Test') {
                steps {
                    parallel (
                        "unit tests": { bat "mvn test" },
                        "integration tests": { bat "mvn integration-test" }
                    )
                }
            }

            stage('Deploy To Dev'){
                steps {
                    deploy()
                }
            }

            stage('Deploy To System'){
                steps {
                    deploy()
                }
            }

            stage('Deploy To Performance'){
                steps {
                    deploy()
                }
            }
            
            stage('Deploy To Pre-Prod'){
	        steps {
	               deploy()
	        }
            }
            
            stage('Deploy To Production'){
	    	  steps {
	    	       deploy()
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
