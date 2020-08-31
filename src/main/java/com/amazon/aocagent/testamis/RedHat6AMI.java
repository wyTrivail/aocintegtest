package com.amazon.aocagent.testamis;

public class RedHat6AMI extends RedHatAMI {
  public RedHat6AMI(String amiId) {
    super(amiId);
  }

  @Override
  public String getIptablesCommand() {
    return "sudo iptables -I INPUT -p tcp -m tcp --dport 55680 -j ACCEPT"
        + "&& sudo service iptables save";
  }
}
