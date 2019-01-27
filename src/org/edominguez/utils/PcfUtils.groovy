package org.edominguez.utils;

class PcfUtils{
  static domain;
  static org;
  static space;
  
  PcfUtils(domain,org){
    this.domain = domain;
    this.org = org;  
  }
  
  static login(userName,pass,env){
    this.space = env;
    bat "cf api ${domain}
    bat "cf login -u ${userName} -p ${pass] -o ${org} -space ${space}"  
  }
  
  static deploy(){
    bat "cf push"
    
  }
  
}
