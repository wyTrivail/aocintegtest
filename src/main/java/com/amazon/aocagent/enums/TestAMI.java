package com.amazon.aocagent.enums;

import com.amazon.aocagent.testamis.A1AmazonLinuxAMI;
import com.amazon.aocagent.testamis.A1RedHatAMI;
import com.amazon.aocagent.testamis.A1SuseAMI;
import com.amazon.aocagent.testamis.A1UbuntuAMI;
import com.amazon.aocagent.testamis.AmazonLinuxAMI;
import com.amazon.aocagent.testamis.CentosAMI;
import com.amazon.aocagent.testamis.DebianAMI;
import com.amazon.aocagent.testamis.EcsOptimizedAMI;
import com.amazon.aocagent.testamis.ITestAMI;
import com.amazon.aocagent.testamis.RedHatAMI;
import com.amazon.aocagent.testamis.SuseAMI;
import com.amazon.aocagent.testamis.UbuntuAMI;
import lombok.Getter;

@Getter
public enum TestAMI {
  // Amazonlinux
  AMAZON_LINUX(new AmazonLinuxAMI("ami-0a07be880014c7b8e")),
  AMAZON_LINUX2(new AmazonLinuxAMI("ami-0873b46c45c11058d")),
  A1_AMAZON_LINUX(new A1AmazonLinuxAMI("ami-091a6d6d0ed7b35fd")),

  // Suse
  SUSE_15(new SuseAMI("ami-063c2d222d223d0e9")),
  SUSE_12(new SuseAMI("ami-811794f9")),
  A1_SUSE_15(new A1SuseAMI("ami-0bfc92b18fd79372c")),

  // redhat
  REDHAT_8_0(new RedHatAMI("ami-079596bf7a949ddf8")),
  REDHAT_7_6(new RedHatAMI("ami-0a7e1ebfee7a4570e")),
  REDHAT_7_5(new RedHatAMI("ami-28e07e50")),
  REDHAT_7_4(new RedHatAMI("ami-9fa343e7")),
  REDHAT_7_2(new RedHatAMI("ami-775e4f16")),
  REDHAT_7_0(new RedHatAMI("ami-212e0911")),
  REDHAT_6_5(new RedHatAMI("ami-e08efbd0")),
  A1_REDHAT_8_0(new A1RedHatAMI("ami-0f7a968a2c17fb48b")),
  A1_REDHAT_7_0(new A1RedHatAMI("ami-0e00026dd0f3688e2")),

  // centos
  CENTOS_7_6(new CentosAMI("ami-00d4ae0422100c609")),
  CENTOS_7_2(new CentosAMI("ami-91ea11f1")),
  CENTOS_7_0(new CentosAMI("ami-f4533694")),
  CENTOS_6_8(new CentosAMI("ami-d7711cb7")),
  CENTOS_6_5(new CentosAMI("ami-4dc28f7d")),
  CENTOS_6_4(new CentosAMI("ami-50a73d60")),
  CENTOS_6_3(new CentosAMI("ami-0e60eb3e")),
  CENTOS_6_0(new CentosAMI("ami-e9503589")),

  // debian
  DEBIAN_10(new DebianAMI("ami-0bb8fb45872332e66")),
  DEBIAN_9(new DebianAMI("ami-0ccb963e85bc5c856")),
  DEBIAN_8(new DebianAMI("ami-fde96b9d")),

  // ubuntu
  UBUNTU_18_04(new UbuntuAMI("ami-0edf3b95e26a682df")),
  UBUNTU_16_04(new UbuntuAMI("ami-6e1a0117")),
  UBUNTU_14_04(new UbuntuAMI("ami-718c6909")),
  A1_UBUNTU_18_04(new A1UbuntuAMI("ami-0db180c518750ee4f")),
  A1_UBUNTU_16_04(new A1UbuntuAMI("ami-05e1b2aec3b47890f")),

  // ECS Optimized AMI
  ECS_OPTIMIZED(new EcsOptimizedAMI("ami-004e1655142a7ea0d")),
  ;

  private ITestAMI testAMIObj;

  TestAMI(ITestAMI testAMI) {
    this.testAMIObj = testAMI;
  }
}
