package com.amazon.aocagent.validators;

import com.amazon.aocagent.models.Context;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class BatchedValidator {
  List<IValidator> metricValidatorList;

  public BatchedValidator(List<IValidator> metricValidatorList) {
    this.metricValidatorList = metricValidatorList;
  }

  /**
   * validate runs all the validators configured.
   *
   * @param context the context object
   * @throws Exception when the validation fails
   */
  public void validate(Context context) throws Exception {
    for (IValidator metricValidator : this.metricValidatorList) {
      metricValidator.validate(context);
    }

    log.info("Validation is passed!!!");
  }
}
