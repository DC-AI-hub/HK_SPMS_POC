import React, { createContext, useContext, useReducer, useEffect, useCallback, useMemo } from 'react';
import systemService from '../api/system/systemService';
import menuService from '../api/system/menuService';
import staticsService from '../api/system/staticsService';
import roleService from '../api/idm/roleService';

// Define action types
const ACTION_TYPES = {
  SET_LOADING: 'SET_LOADING',
  SET_ERROR: 'SET_ERROR',
  SET_SYSTEM_CONFIG: 'SET_SYSTEM_CONFIG',
  SET_MENU_CONFIG: 'SET_MENU_CONFIG',
  SET_THEME_CONFIG: 'SET_THEME_CONFIG',
  SET_ROLES: 'SET_ROLES',
  UPDATE_SYSTEM_SETTING: 'UPDATE_SYSTEM_SETTING',
  ADD_MENU_ITEM: 'ADD_MENU_ITEM',
  UPDATE_MENU_ITEM: 'UPDATE_MENU_ITEM',
  DELETE_MENU_ITEM: 'DELETE_MENU_ITEM',
  SET_ACTIVE_THEME: 'SET_ACTIVE_THEME',
  SET_LAST_UPDATED: 'SET_LAST_UPDATED'
};

// Initial state
const initialState = {
  loading: false,
  error: null,
  systemConfig: [],
  menuConfig: [],
  themeConfig: [],
  roles: [],
  lastUpdated: null
};

// Reducer function
function configReducer(state, action) {
  switch (action.type) {
    case ACTION_TYPES.SET_LOADING:
      return { ...state, loading: action.payload };
    
    case ACTION_TYPES.SET_ERROR:
      return { ...state, error: action.payload, loading: false };
    
    case ACTION_TYPES.SET_SYSTEM_CONFIG:
      return { ...state, systemConfig: action.payload, error: null };
    
    case ACTION_TYPES.SET_MENU_CONFIG:
      return { ...state, menuConfig: action.payload, error: null };
    
    case ACTION_TYPES.SET_THEME_CONFIG:
      return { ...state, themeConfig: action.payload, error: null };
    
    case ACTION_TYPES.SET_ROLES:
      return { ...state, roles: action.payload };
    
    case ACTION_TYPES.UPDATE_SYSTEM_SETTING:
      const updatedSystemConfig = state.systemConfig.map(setting =>
        setting.key === action.payload.key ? action.payload : setting
      );
      return { ...state, systemConfig: updatedSystemConfig };
    
    case ACTION_TYPES.ADD_MENU_ITEM:
      return { ...state, menuConfig: [...state.menuConfig, action.payload] };
    
    case ACTION_TYPES.UPDATE_MENU_ITEM:
      const updatedMenuConfig = state.menuConfig.map(item =>
        item.id === action.payload.id ? action.payload : item
      );
      return { ...state, menuConfig: updatedMenuConfig };
    
    case ACTION_TYPES.DELETE_MENU_ITEM:
      const filteredMenuConfig = state.menuConfig.filter(item => item.id !== action.payload);
      return { ...state, menuConfig: filteredMenuConfig };
    
    case ACTION_TYPES.SET_ACTIVE_THEME:
      const updatedThemeConfig = state.themeConfig.map(theme => ({
        ...theme,
        isDefault: theme.id === action.payload
      }));
      return { ...state, themeConfig: updatedThemeConfig };
    
    case ACTION_TYPES.SET_LAST_UPDATED:
      return { ...state, lastUpdated: action.payload };
    
    default:
      return state;
  }
}

// Create context
const ConfigContext = createContext();

// Context provider component
export function ConfigProvider({ children }) {
  const [state, dispatch] = useReducer(configReducer, initialState);

  // Action creators - use useCallback to prevent unnecessary re-renders
  const setLoading = useCallback((loading) => dispatch({ type: ACTION_TYPES.SET_LOADING, payload: loading }), [dispatch]);
  const setError = useCallback((error) => dispatch({ type: ACTION_TYPES.SET_ERROR, payload: error }), [dispatch]);
  const setSystemConfig = useCallback((config) => dispatch({ type: ACTION_TYPES.SET_SYSTEM_CONFIG, payload: config }), [dispatch]);
  const setMenuConfig = useCallback((config) => dispatch({ type: ACTION_TYPES.SET_MENU_CONFIG, payload: config }), [dispatch]);
  const setThemeConfig = useCallback((config) => dispatch({ type: ACTION_TYPES.SET_THEME_CONFIG, payload: config }), [dispatch]);
  const updateSystemSetting = useCallback((setting) => dispatch({ type: ACTION_TYPES.UPDATE_SYSTEM_SETTING, payload: setting }), [dispatch]);
  const addMenuItem = useCallback((item) => dispatch({ type: ACTION_TYPES.ADD_MENU_ITEM, payload: item }), [dispatch]);
  const updateMenuItem = useCallback((item) => dispatch({ type: ACTION_TYPES.UPDATE_MENU_ITEM, payload: item }), [dispatch]);
  const deleteMenuItem = useCallback((id) => dispatch({ type: ACTION_TYPES.DELETE_MENU_ITEM, payload: id }), [dispatch]);
  const setActiveTheme = useCallback((themeId) => dispatch({ type: ACTION_TYPES.SET_ACTIVE_THEME, payload: themeId }), [dispatch]);
  const setLastUpdated = useCallback((timestamp) => dispatch({ type: ACTION_TYPES.SET_LAST_UPDATED, payload: timestamp }), [dispatch]);
  const setRoles = useCallback((roles) => dispatch({ type: ACTION_TYPES.SET_ROLES, payload: roles }), [dispatch]);

  // Load system configuration
  const loadSystemConfig = useCallback(async () => {
    try {
      setLoading(true);
      const response = await systemService.getSystemConfig();
      setSystemConfig(response.data);
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to load system configuration');
    } finally {
      setLoading(false);
    }
  }, [setLoading, setSystemConfig, setError]);

  // Load menu configuration
  const loadMenuConfig = useCallback(async () => {
    try {
      setLoading(true);
      const response = await menuService.getAllMenus({ page: 0, size: 1000, sort: 'displayOrder,asc' });
      setMenuConfig(response.data.content || response.data);
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to load menu configuration');
    } finally {
      setLoading(false);
    }
  }, [setLoading, setMenuConfig, setError]);

  // Load theme configuration
  const loadThemeConfig = useCallback(async () => {
    try {
      setLoading(true);
      const response = await staticsService.getThemes();
      setThemeConfig(response.data);
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to load theme configuration');
    } finally {
      setLoading(false);
    }
  }, [setLoading, setThemeConfig, setError]);

  // Update system setting
  const updateSystemSettingApi = useCallback(async (key, value) => {
    try {
      setLoading(true);
      const response = await systemService.updateSystemConfig(key, value);
      updateSystemSetting(response.data);
      setLastUpdated(new Date().toISOString());
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to update system setting');
      throw error;
    } finally {
      setLoading(false);
    }
  }, [setLoading, updateSystemSetting, setLastUpdated, setError]);

  // Create menu item
  const createMenuItem = useCallback(async (menuData) => {
    try {
      setLoading(true);
      const response = await menuService.createMenu(menuData);
      addMenuItem(response.data);
      setLastUpdated(new Date().toISOString());
      return response.data;
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to create menu item');
      throw error;
    } finally {
      setLoading(false);
    }
  }, [setLoading, addMenuItem, setLastUpdated, setError]);

  // Update menu item
  const updateMenuItemApi = useCallback(async (id, menuData) => {
    try {
      setLoading(true);
      const response = await menuService.updateMenu(id, menuData);
      updateMenuItem(response.data);
      setLastUpdated(new Date().toISOString());
      return response.data;
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to update menu item');
      throw error;
    } finally {
      setLoading(false);
    }
  }, [setLoading, updateMenuItem, setLastUpdated, setError]);

  // Delete menu item
  const deleteMenuItemApi = useCallback(async (id) => {
    try {
      setLoading(true);
      await menuService.deleteMenu(id);
      deleteMenuItem(id);
      setLastUpdated(new Date().toISOString());
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to delete menu item');
      throw error;
    } finally {
      setLoading(false);
    }
  }, [setLoading, deleteMenuItem, setLastUpdated, setError]);

  // Set active theme
  const setActiveThemeApi = useCallback(async (themeId) => {
    try {
      setLoading(true);
      await staticsService.setActiveTheme(themeId);
      setActiveTheme(themeId);
      setLastUpdated(new Date().toISOString());
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to set active theme');
      throw error;
    } finally {
      setLoading(false);
    }
  }, [setLoading, setActiveTheme, setLastUpdated, setError]);

  // Load roles
  const loadRoles = useCallback(async () => {
    try {
      dispatch({ type: ACTION_TYPES.SET_LOADING, payload: true });
      const response = await roleService.getAll({ page: 0, size: 1000, sort: 'name,asc' });
      console.log('Roles response:', response);
      
      // Handle different response formats
      let rolesData = [];
      if (Array.isArray(response)) {
        rolesData = response;
      } else if (response && Array.isArray(response.content)) {
        rolesData = response.content;
      } else if (response && response.data && Array.isArray(response.data)) {
        rolesData = response.data;
      } else if (response && response.data && Array.isArray(response.data.content)) {
        rolesData = response.data.content;
      }
      
      console.log('Processed roles data:', rolesData);
      setRoles(rolesData);
    } catch (error) {
      console.error('Error loading roles:', error);
      setError(error.response?.data?.message || 'Failed to load roles');
    } finally {
      dispatch({ type: ACTION_TYPES.SET_LOADING, payload: false });
    }
  }, [setError]);

  // Refresh all configuration
  const refreshConfig = useCallback(async () => {
    try {
      setLoading(true);
      await Promise.all([
        loadSystemConfig(),
        loadMenuConfig(),
        loadThemeConfig(),
        loadRoles()
      ]);
      setLastUpdated(new Date().toISOString());
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to refresh configuration');
    } finally {
      setLoading(false);
    }
  }, [setLoading, loadSystemConfig, loadMenuConfig, loadThemeConfig, setLastUpdated, setError]);

  // Load all configuration on mount - run only once
  useEffect(() => {
    refreshConfig();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []); // Empty dependency array ensures this runs only once on mount

  // Memoize context value to prevent unnecessary re-renders
  const value = useMemo(() => ({
    // State
    loading: state.loading,
    error: state.error,
    systemConfig: state.systemConfig,
    menuConfig: state.menuConfig,
    themeConfig: state.themeConfig,
    roles: state.roles,
    lastUpdated: state.lastUpdated,

    // Actions
    loadSystemConfig,
    loadMenuConfig,
    loadThemeConfig,
    loadRoles,
    updateSystemSetting: updateSystemSettingApi,
    createMenuItem,
    updateMenuItem: updateMenuItemApi,
    deleteMenuItem: deleteMenuItemApi,
    setActiveTheme: setActiveThemeApi,
    refreshConfig
  }), [
    state.loading,
    state.error,
    state.systemConfig,
    state.menuConfig,
    state.themeConfig,
    state.roles,
    state.lastUpdated,
    loadSystemConfig,
    loadMenuConfig,
    loadThemeConfig,
    loadRoles,
    updateSystemSettingApi,
    createMenuItem,
    updateMenuItemApi,
    deleteMenuItemApi,
    setActiveThemeApi,
    refreshConfig
  ]);

  return (
    <ConfigContext.Provider value={value}>
      {children}
    </ConfigContext.Provider>
  );
}

// Custom hook to use the config context
export function useConfig() {
  const context = useContext(ConfigContext);
  if (!context) {
    throw new Error('useConfig must be used within a ConfigProvider');
  }
  return context;
}

export default ConfigContext;
