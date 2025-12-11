import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

// English translations
import enCommon from './locales/en/common.json';
import enHeader from './locales/en/header.json';
import enSidebar from './locales/en/sidebar.json';
import enOrganization from './locales/en/organization.json';
import enProcess from './locales/en/process.json';
import enForm from './locales/en/form.json';
import enUserProcess from './locales/en/userProcess.json';
import enAccess from './locales/en/access.json';
import enStat from "./locales/en/statistics.json"
import enSystem from "./locales/en/system.json"


// Chinese (Simplified) translations
import zhCommon from './locales/zh/common.json';
import zhHeader from './locales/zh/header.json';
import zhSidebar from './locales/zh/sidebar.json';
import zhOrganization from './locales/zh/organization.json';
import zhProcess from './locales/zh/process.json';
import zhForm from './locales/zh/form.json';
import zhUserProcess from "./locales/zh/userProcess.json";
import zhAccess from './locales/zh/access.json';
import zhStat from "./locales/zh/statistics.json"
import zhSystem from "./locales/zh/system.json"


// Chinese (Traditional) translations
import zhTRCommon from './locales/zh-TR/common.json';
import zhTRHeader from './locales/zh-TR/header.json';
import zhTRSidebar from './locales/zh-TR/sidebar.json';
import zhTROrganization from './locales/zh-TR/organization.json';
import zhTRProcess from './locales/zh-TR/process.json';
import zhTRForm from './locales/zh-TR/form.json';
import zhTrUserProcess from "./locales/zh-TR/userProcess.json";
import zhTRAccess from './locales/zh-TR/access.json';
import zhTRStat from "./locales/zh-TR/statistics.json"
import zhTRSystem from "./locales/zh-TR/system.json"


i18n
  .use(initReactI18next)
  .init({
    resources: {
      en: {
        common: enCommon,
        header: enHeader,
        sidebar: enSidebar,
        organization: enOrganization,
        process: enProcess,
        form: enForm,
        userProcess: enUserProcess,
        access: enAccess,
        statistics: enStat,
        system: enSystem
      },
      zh: {
        common: zhCommon,
        header: zhHeader,
        sidebar: zhSidebar,
        organization: zhOrganization,
        process: zhProcess,
        form: zhForm,
        userProcess: zhUserProcess,
        access: zhAccess,
        statistics: zhStat,
        system: zhSystem
      },
      'zh-TR': {
        common: zhTRCommon,
        header: zhTRHeader,
        sidebar: zhTRSidebar,
        organization: zhTROrganization,
        process: zhTRProcess,
        form: zhTRForm,
        userProcess: zhTrUserProcess,
        access: zhTRAccess,
        statistics: zhTRStat,
        system: zhTRSystem
      }
    },

    lng: 'en',
    fallbackLng: 'en',
    interpolation: {
      escapeValue: false
    }
  });

export default i18n;
