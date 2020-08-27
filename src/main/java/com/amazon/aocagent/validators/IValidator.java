package com.amazon.aocagent.validators;

import com.amazon.aocagent.models.Context;

public interface IValidator {
  void init(Context context) throws Exception;

  void validate() throws Exception;
}
