package com.amazon.aocagent.models;

import com.amazon.aocagent.enums.Architecture;
import com.amazonaws.services.ec2.model.TagSpecification;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EC2InstanceParams {
  String amiId;
  String sshKeyName;
  String securityGrpName;
  String iamRoleName;
  String userData;
  Architecture arch;
  TagSpecification tagSpecification;
}
