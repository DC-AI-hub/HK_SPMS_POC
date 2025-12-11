import { Page } from '@playwright/test';
import { LOGIN_CREDENTIALS, BASE_URL } from './testData';

export class LoginHelper {
  constructor(private page: Page) {}

  async login() {
    console.log('📍 开始登录流程');
    
    // 访问首页
    await this.page.goto(BASE_URL);
    await this.page.waitForTimeout(5000);
    
    // 检查是否需要登录
    const hasLoginButton = await this.page.locator('button:has-text("登录"), button:has-text("单点登录")').count();
    if (hasLoginButton > 0) {
      console.log('📍 执行登录操作');
      await this.page.click('button:has-text("登录"), button:has-text("单点登录")').first();
      await this.page.waitForTimeout(5000);
      
      // 检查是否有Keycloak登录页面
      const usernameInput = this.page.locator('#username');
      if (await usernameInput.count() > 0) {
        await this.page.fill('#username', LOGIN_CREDENTIALS.username);
        await this.page.fill('#password', LOGIN_CREDENTIALS.password);
        await this.page.click('#kc-login');
        await this.page.waitForTimeout(10000);
      }
    }
    
    console.log('✅ 登录完成');
  }

  async navigateToOrganizationModule() {
    console.log('📍 导航到组织管理模块');
    
    // 点击菜单图标打开侧边栏
    console.log('📍 点击菜单图标');
    const menuIcon = this.page.locator('svg[data-testid="MenuIcon"], svg:has(path[d*="M3 18h18v-2H3zm0-5h18v-2H3zm0-7v2h18V6z"])');
    await menuIcon.click();
    await this.page.waitForTimeout(2000);
    
    // 点击Organization导航
    console.log('📍 点击Organization导航');
    const organizationNav = this.page.locator('span:has-text("Organization")').first();
    await organizationNav.click();
    await this.page.waitForTimeout(5000);
    
    // 确保在公司管理标签页
    const companyTab = this.page.locator('tab:has-text("公司管理"), button:has-text("公司管理")');
    if (await companyTab.count() > 0) {
      await companyTab.click();
      await this.page.waitForTimeout(3000);
    }
    
    console.log('✅ 已导航到公司管理页面');
  }

  async logout() {
    // 实现登出逻辑（如果需要）
    const logoutButton = this.page.locator('button:has-text("登出"), button:has-text("Logout")');
    if (await logoutButton.count() > 0) {
      await logoutButton.click();
      await this.page.waitForTimeout(2000);
    }
  }
} 