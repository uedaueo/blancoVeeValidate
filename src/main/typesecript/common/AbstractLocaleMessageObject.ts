import VueI18n from "vue-i18n";
import LocaleMessageObject = VueI18n.LocaleMessageObject;

export abstract class AbstractLocaleMessageObject
  implements LocaleMessageObject {
  [key: string]: VueI18n.LocaleMessage;
}
