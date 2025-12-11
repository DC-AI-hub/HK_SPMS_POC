import { Page, Locator } from '@playwright/test';

export class CompanyPage {
  readonly page: Page;
  readonly createButton: Locator;
  readonly importButton: Locator;
  readonly operationButton: Locator;
  readonly deleteSelectedButton: Locator;
  readonly searchInput: Locator;
  readonly dataGrid: Locator;
  readonly selectAllCheckbox: Locator;
  
  constructor(page: Page) {
    this.page = page;
    this.createButton = page.locator('button:has-text("创建公司"), button:has-text("Create Company")');
    this.importButton = page.locator('button:has-text("导入"), button:has-text("Import")');
    this.operationButton = page.locator('button:has-text("操作"), button:has-text("Operation")');
    this.deleteSelectedButton = page.locator('button:has-text("删除选中"), button:has-text("Delete")');
    this.searchInput = page.locator('input[placeholder*="搜索"], input[placeholder*="Search"]');
    this.dataGrid = page.locator('[role="grid"], table');
    this.selectAllCheckbox = page.locator('[role="grid"] input[type="checkbox"]').first();
  }

  async goto() {
    await this.page.goto('http://localhost:5173');
    await this.page.waitForTimeout(5000);
  }

  async navigateToCompanyModule() {
    // 导航到组织管理页面
    await this.page.click('text=组织管理, text=Organization');
    await this.page.waitForTimeout(5000);
    
    // 确保在公司管理标签页
    const companyTab = this.page.locator('tab:has-text("公司管理"), button:has-text("公司管理")');
    if (await companyTab.count() > 0) {
      await companyTab.click();
      await this.page.waitForTimeout(3000);
    }
  }

  async createCompany(companyData: {
    name: string;
    description: string;
    companyType: string;
    active?: boolean;
  }) {
    await this.createButton.click();
    await this.page.waitForTimeout(3000);

    const dialog = this.page.locator('[role="dialog"]');
    
    // 填写公司信息
    await dialog.locator('input[name="name"], input[id*="name"]').fill(companyData.name);
    await dialog.locator('textarea[name="description"], input[name="description"]').fill(companyData.description);
    
    // 选择公司类型
    const typeSelect = dialog.locator('div:has-text("公司类型") + *, [name="companyType"]');
    if (await typeSelect.count() > 0) {
      await typeSelect.click();
      await this.page.waitForTimeout(1000);
      
      const typeOption = this.page.locator(`li:has-text("${companyData.companyType}"), option:has-text("${companyData.companyType}")`);
      if (await typeOption.count() > 0) {
        await typeOption.click();
      }
    }
    
    // 设置Active状态
    if (companyData.active !== undefined) {
      const activeCheckbox = dialog.locator('input[name="active"], input[type="checkbox"]');
      if (await activeCheckbox.count() > 0) {
        if (companyData.active && !(await activeCheckbox.isChecked())) {
          await activeCheckbox.check();
        } else if (!companyData.active && (await activeCheckbox.isChecked())) {
          await activeCheckbox.uncheck();
        }
      }
    }
    
    // 提交表单
    await dialog.locator('button[type="submit"], button:has-text("提交"), button:has-text("创建")').click();
    await this.page.waitForTimeout(5000);
  }

  async searchCompany(searchText: string) {
    if (await this.searchInput.count() > 0) {
      await this.searchInput.fill(searchText);
      await this.page.waitForTimeout(2000);
    }
  }

  async clearSearch() {
    if (await this.searchInput.count() > 0) {
      await this.searchInput.fill('');
      await this.page.waitForTimeout(1000);
    }
  }

  async selectAllCompanies() {
    if (await this.selectAllCheckbox.count() > 0) {
      await this.selectAllCheckbox.click();
      await this.page.waitForTimeout(1000);
    }
  }

  async getRowCount() {
    return await this.page.locator('[role="grid"] [role="row"]').count();
  }

  async getFirstEditButton() {
    return this.page.locator('button:has-text("编辑"), button:has-text("Edit")').first();
  }

  async getFirstDeleteButton() {
    return this.page.locator('button:has-text("删除"), button:has-text("Delete")').first();
  }

  async isCompanyVisible(companyName: string) {
    const companyRow = this.page.locator(`text=${companyName}`);
    return await companyRow.isVisible();
  }

  async waitForCompanyToAppear(companyName: string, timeout = 10000) {
    const companyRow = this.page.locator(`text=${companyName}`);
    await companyRow.waitFor({ state: 'visible', timeout });
  }
} 