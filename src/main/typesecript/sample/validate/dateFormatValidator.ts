import moment from "moment";

export const dateFormatValidator = (value: any, params: any[] | Record<string, any>) => {
  const format = (params as { [key: string]: string }).format;
  return moment(value, format, true).isValid();
};
