export interface CompanyTestData {
  name: string;
  description: string;
  companyType: 'Group' | 'Business Entity' | 'Vendor' | 'Customer' | 'Other';
  active: boolean;
}

export function generateTestCompany(): CompanyTestData {
  const timestamp = Date.now().toString().slice(-5);
  return {
    name: `测试公司-${timestamp}`,
    description: `自动化测试创建的公司 - ${timestamp}`,
    companyType: 'Group',
    active: true
  };
}

export function generateBusinessEntityCompany(): CompanyTestData {
  const timestamp = Date.now().toString().slice(-5);
  return {
    name: `商业实体-${timestamp}`,
    description: `测试商业实体公司 - ${timestamp}`,
    companyType: 'Business Entity',
    active: true
  };
}

export function generateVendorCompany(): CompanyTestData {
  const timestamp = Date.now().toString().slice(-5);
  return {
    name: `供应商-${timestamp}`,
    description: `测试供应商公司 - ${timestamp}`,
    companyType: 'Vendor',
    active: true
  };
}

export const LOGIN_CREDENTIALS = {
  username: 'admin@hkex.com',
  password: '123456'
};

export const BASE_URL = 'http://localhost:5173'; 