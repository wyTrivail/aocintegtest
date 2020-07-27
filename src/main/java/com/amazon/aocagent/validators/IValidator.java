package com.amazon.aocagent.validators;

import com.amazon.aocagent.models.Context;

public interface IValidator {
  void validate(Context context) throws Exception;
}
