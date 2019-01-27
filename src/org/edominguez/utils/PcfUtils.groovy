package org.edominguez.utils;

class PcfUtils{
  static domain;
  static org;
  static space;
  
  PcfUtils(domain,org,env){
    this.domain = domain;
    this.org = org;
    this.space = env;
  }
  
  static login(userName,pass){
    bat "cf api ${domain}
    bat "cf login -u ${userName} -p ${pass] -o ${org} -space ${space}"  
  }
  
  static deploy(){
    bat "cf push"
    
  }
  
}
