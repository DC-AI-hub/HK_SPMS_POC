import { useConfig as useConfigContext } from '../contexts/ConfigContext';
import {
  transformMenuConfig,
  filterMenusBySearch,
  getTopLevelMenus,
  isValidMenuConfig
} from '../utils/menuUtils';

/**
 * Custom hook for accessing the configuration context
 * Provides easy access to system configuration state and actions
 * 
 * @returns {Object} Configuration context with state and methods
 */
export function useConfig() {
  const context = useConfigContext();
  
  /**
   * Get system setting by key
   * @param {string} key - The setting key to retrieve
   * @returns {Object|null} The setting object or null if not found
   */
  const getSystemSetting = (key) => {
    return context.systemConfig.find(setting => setting.key === key) || null;
  };

  /**
   * Get system setting value by key
   * @param {string} key - The setting key to retrieve
   * @returns {any} The setting value or null if not found
   */
  const getSystemSettingValue = (key) => {
    const setting = getSystemSetting(key);
    return setting ? setting.value : null;
  };

  /**
   * Get menu items by parent ID (for building hierarchy)
   * @param {number} parentId - The parent menu ID
   * @returns {Array} Array of menu items with the specified parent ID
   */
  const getMenuItemsByParent = (parentId) => {
    return context.menuConfig.filter(item => item.parentMenuId === parentId);
  };

  /**
   * Get active theme
   * @returns {Object|null} The active theme object or null if not found
   */
  const getActiveTheme = () => {
    return context.themeConfig.find(theme => theme.isDefault) || null;
  };

  /**
   * Check if a specific menu item exists
   * @param {number} menuId - The menu ID to check
   * @returns {boolean} True if the menu item exists
   */
  const menuItemExists = (menuId) => {
    return context.menuConfig.some(item => item.id === menuId);
  };

  /**
   * Get theme by ID
   * @param {number} themeId - The theme ID to retrieve
   * @returns {Object|null} The theme object or null if not found
   */
  const getThemeById = (themeId) => {
    return context.themeConfig.find(theme => theme.id === themeId) || null;
  };

  /**
   * Check if configuration data is loaded
   * @returns {boolean} True if all configuration data is loaded
   */
  const isConfigLoaded = () => {
    return !context.loading && 
           context.systemConfig.length > 0 && 
           context.menuConfig.length > 0 && 
           context.themeConfig.length > 0;
  };

  /**
   * Check if there are any errors in the configuration
   * @returns {boolean} True if there are configuration errors
   */
  const hasConfigErrors = () => {
    return context.error !== null;
  };

  /**
   * Get transformed menu configuration for frontend use
   * @returns {Array} Transformed menu items
   */
  const getTransformedMenuConfig = () => {
    return transformMenuConfig(context.menuConfig);
  };

  /**
   * Filter menu items by search term
   * @param {string} searchTerm - Search term to filter by
   * @returns {Array} Filtered menu items
   */
  const getFilteredMenus = (searchTerm) => {
    const transformedMenu = getTransformedMenuConfig();
    return filterMenusBySearch(transformedMenu, searchTerm);
  };

  /**
   * Get top-level menu items (no parent)
   * @returns {Array} Top-level menu items
   */
  const getTopLevelMenuItems = () => {
    const transformedMenu = getTransformedMenuConfig();
    return getTopLevelMenus(transformedMenu);
  };

  /**
   * Check if menu configuration is valid and contains items
   * @returns {boolean} True if menu configuration is valid
   */
  const isMenuConfigValid = () => {
    return isValidMenuConfig(context.menuConfig);
  };

  return {
    // State from context
    loading: context.loading,
    error: context.error,
    systemConfig: context.systemConfig,
    menuConfig: context.menuConfig,
    themeConfig: context.themeConfig,
    roles: context.roles,
    lastUpdated: context.lastUpdated,

    // Methods from context
    loadSystemConfig: context.loadSystemConfig,
    loadMenuConfig: context.loadMenuConfig,
    loadThemeConfig: context.loadThemeConfig,
    loadRoles: context.loadRoles,
    updateSystemSetting: context.updateSystemSetting,
    createMenuItem: context.createMenuItem,
    updateMenuItem: context.updateMenuItem,
    deleteMenuItem: context.deleteMenuItem,
    setActiveTheme: context.setActiveTheme,
    refreshConfig: context.refreshConfig,

    // Enhanced methods
    getSystemSetting,
    getSystemSettingValue,
    getMenuItemsByParent,
    getActiveTheme,
    menuItemExists,
    getThemeById,
    isConfigLoaded,
    hasConfigErrors,

    // New menu utility methods
    getTransformedMenuConfig,
    getFilteredMenus,
    getTopLevelMenuItems,
    isMenuConfigValid
  };
}

export default useConfig;
