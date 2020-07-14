import { VeeValidateConfig } from "vee-validate/dist/types/config";
import i18n from "@/i18n";
import { RuleParamSchema } from "vee-validate/dist/types/types";

export const validateConfig: Partial<VeeValidateConfig> = {
  defaultMessage: (field, params) => {
    if (params === undefined) {
      return field;
    }
    params._field_ = i18n.t(`labels.${field}`);
    return i18n.t(`validation.${params._rule_}`, params) as string;
  }
};

export const customMessage = (
  field: string,
  values?: Record<string, any>,
  params?: RuleParamSchema[]
) => {
  if (values === undefined) {
    return field;
  }
  if (params !== undefined) {
    const iValues = values as { [key: string]: any };
    for (const paramTag in params) {
      values[paramTag] = iValues[paramTag];
    }
  }
  values._field_ = i18n.t(`labels.${field}`);
  return i18n.t(`customValidation.${values._rule_}`, values) as string;
};
